/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.migrate.sacer.os.beans.datispecvers.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_PAD5DIGITS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_UPD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.normalizingKey;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.IMigrateOsDatiSpecVersS3Service;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.ISacerDatiSpecVersDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniDatiSpec;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniUnitaDoc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

/**
 * Servizio per la migrazione dei dati specifici verso un bucket S3. Questa classe implementa la
 * logica per creare file ZIP contenenti i dati specifici, calcolare l'URN, caricare i file su S3 e
 * gestire eventuali errori.
 */
@ApplicationScoped
public class MigrateOsDatiSpecVersS3Service extends MigrateOsS3Abstract
	implements IMigrateOsDatiSpecVersS3Service {

    private static final Logger logger = LoggerFactory
	    .getLogger(MigrateOsDatiSpecVersS3Service.class);

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend; // Nome del backend S3 configurato.

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName; // Nome del bucket S3 dove caricare i file.

    @Inject
    ISacerDatiSpecVersDao sacerDatiSpecDao; // DAO per l'accesso ai dati specifici.

    @Inject
    IMigrateSacerDao sacerDao;

    /**
     * Esegue la migrazione dei dati specifici verso S3.
     *
     * @param idSacerBackend    ID del backend Sacer.
     * @param idVersIniUnitaDoc ID dell'unità documento iniziale.
     * @param delete            Flag per indicare se eliminare i dati sorgente dopo la migrazione.
     *
     * @return Risorsa di storage creata su S3.
     *
     * @throws AppMigrateOsS3Exception In caso di errore durante la migrazione.
     */
    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idVersIniUnitaDoc,
	    Boolean delete) throws AppMigrateOsS3Exception {
	AroVersIniUnitaDoc aroVersIniUnitaDoc = sacerDatiSpecDao
		.findAroVersIniUnitaDocById(idVersIniUnitaDoc);
	// Recupera i dati specifici associati all'unità documento.
	List<AroVersIniDatiSpec> datiSpec = aroVersIniUnitaDoc.getAroVersIniDatiSpecs();

	IObjectStorageResource osResource = null;
	try {
	    // Crea un blob XML organizzato per chiavi.
	    Map<DatiSpecLinkOsKeyMap, Map<String, String>> xmlBlob = createDatiSpecVersBlob(
		    datiSpec);
	    // Calcola l'URN per i dati specifici.
	    String tmpUrn = calculateUrnDatiSpecVers(aroVersIniUnitaDoc);

	    // Crea e carica i file ZIP su S3 per ogni entry del blob.
	    for (Map.Entry<DatiSpecLinkOsKeyMap, Map<String, String>> entry : xmlBlob.entrySet()) {
		osResource = createResourcesUpdDatiSpecUd(tmpUrn, entry,
			datiSpec.get(0).getOrgStrut().getIdStrut(), idSacerBackend);
	    }

	    // Elimina i dati sorgente se richiesto.
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		deleteDatiSpecVers(datiSpec);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .osresource(osResource)
		    .message("Errore nella migrazione per idVersIniUnitaDoc {0,number,#}",
			    idVersIniUnitaDoc)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message("Errore nella migrazione per idVersIniUnitaDoc {0,number,#}",
			    idVersIniUnitaDoc)
		    .build();
	}
    }

    /**
     * Elimina i dati specifici dal database.
     *
     * @param datiSpec Lista di dati specifici da eliminare.
     *
     * @throws AppMigrateOsDeleteSrcException In caso di errore durante l'eliminazione.
     */
    private void deleteDatiSpecVers(List<AroVersIniDatiSpec> datiSpec)
	    throws AppMigrateOsDeleteSrcException {
	List<Long> ids = datiSpec.stream().map(AroVersIniDatiSpec::getIdVersIniDatiSpec).toList();
	sacerDatiSpecDao.deleteBlDatiSpecVers(ids);
	logger.debug("Eliminati dati specifici per gli ID: {}", ids);
    }

    /**
     * Crea un blob XML organizzato per chiavi a partire dai dati specifici.
     *
     * @param datiSpecList Lista di dati specifici.
     *
     * @return Mappa organizzata per chiavi e contenente i dati XML.
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private Map<DatiSpecLinkOsKeyMap, Map<String, String>> createDatiSpecVersBlob(
	    List<AroVersIniDatiSpec> datiSpecList) throws AppMigrateOsS3Exception {
	try {
	    Map<DatiSpecLinkOsKeyMap, Map<String, String>> blob = new HashMap<>();
	    datiSpecList.forEach(datiSpec -> addToBlob(blob, datiSpec));
	    return blob;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message("Errore durante creazione mappa blob dati specifici").build();
	}
    }

    /**
     * Aggiunge un dato specifico al blob XML.
     *
     * @param blob     Mappa del blob XML.
     * @param datiSpec Dato specifico da aggiungere.
     */
    private void addToBlob(Map<DatiSpecLinkOsKeyMap, Map<String, String>> blob,
	    AroVersIniDatiSpec datiSpec) {
	DatiSpecLinkOsKeyMap key = new DatiSpecLinkOsKeyMap(getKeyEntity(datiSpec),
		datiSpec.getTiEntitaSacer().name());
	blob.computeIfAbsent(key, k -> new HashMap<>()).put(datiSpec.getTiUsoXsd().name(),
		datiSpec.getBlXmlDatiSpec());
    }

    /**
     * Determina la chiave dell'entità per un dato specifico.
     *
     * @param datiSpec Dato specifico.
     *
     * @return Chiave dell'entità.
     */
    private Long getKeyEntity(AroVersIniDatiSpec datiSpec) {
	return switch (datiSpec.getTiEntitaSacer()) {
	case UNI_DOC -> datiSpec.getAroVersIniUnitaDoc().getIdVersIniUnitaDoc();
	case DOC -> datiSpec.getIdVersIniDoc();
	case COMP -> datiSpec.getIdVersIniComp();
	};
    }

    /**
     * Calcola l'URN per i dati specifici.
     *
     *
     * @param aroVersIniUnitaDoc versamento iniziale unita doc
     *
     * @return URN calcolato
     *
     * @throws AppMigrateOsS3Exception
     */
    private String calculateUrnDatiSpecVers(AroVersIniUnitaDoc aroVersIniUnitaDoc)
	    throws AppMigrateOsS3Exception {
	int idx = 0;

	try {
	    AroUnitaDoc unitaDoc = aroVersIniUnitaDoc.getAroUnitaDoc();
	    Object[] result = sacerDao.findNmEnteAndNmStrutByIdStrut(unitaDoc.getIdStrut());

	    return calculateBaseUrn(
		    formattaUrnPartVersatoreKeyOs((String) result[idx], (String) result[++idx]),
		    formattaUrnPartUnitaDocKeyOs(unitaDoc), 1, true, S3_KEY_PAD5DIGITS_FMT);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN dati specifici iniziali unità doc, idVersIniUnitaDoc {0,number,#}",
			    aroVersIniUnitaDoc.getIdVersIniUnitaDoc())
		    .build();
	}
    }

    private String calculateBaseUrn(String versatore, String unitaDoc, long progressivo,
	    boolean pgpad, String padFmtUsed) {
	return MessageFormat.format(S3_KEY_URN_UPD_FMT, versatore, unitaDoc,
		pgpad ? String.format(padFmtUsed, progressivo) : progressivo);
    }

    private String formattaUrnPartVersatoreKeyOs(String nmEnte, String nmStrut) {
	return MessageFormat.format(S3_KEY_VERSATORE_FMT, normalizingKey(nmEnte),
		normalizingKey(nmStrut));
    }

    private String formattaUrnPartUnitaDocKeyOs(AroUnitaDoc ud) {
	return MessageFormat.format(S3_KEY_UD_FMT, normalizingKey(ud.getCdRegistroKeyUnitaDoc()),
		ud.getAaKeyUnitaDoc().toString(), normalizingKey(ud.getCdKeyUnitaDoc()));
    }

    private IObjectStorageResource createResourcesUpdDatiSpecUd(String urn,
	    Map.Entry<DatiSpecLinkOsKeyMap, Map<String, String>> xmlBlob, Long idStrut,
	    Long idBackend) throws IOException, AppMigrateOsS3Exception {
	Path filepath = Files.createTempFile("dati_spec-", ".zip", MigrateUtils.POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osResource = createUpdDatiSpecAndPutOnBucket(urn, filepath,
		    xmlBlob.getValue());
	    sacerDatiSpecDao.saveObjectStorageLinkDatiSpecVers(osResource.getTenant(),
		    osResource.getS3Bucket(), osResource.getS3Key(), idStrut, xmlBlob.getKey(),
		    idBackend);
	    return osResource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per dati specifici aggionamento unità documentaria")
		    .build();
	} finally {
	    Files.deleteIfExists(filepath);
	}
    }

    /**
     * Crea e carica un file ZIP su S3.
     *
     * @param urn      URN del file.
     * @param filepath Percorso del file ZIP temporaneo.
     * @param xmlBlob  Contenuto XML da includere nel file ZIP.
     *
     * @return Risorsa di storage creata su S3.
     *
     * @throws IOException, NoSuchAlgorithmException, SQLException In caso di errori.
     */
    private IObjectStorageResource createUpdDatiSpecAndPutOnBucket(String urn, Path filepath,
	    Map<String, String> xmlBlob) throws IOException, NoSuchAlgorithmException {
	createZipFile(xmlBlob, filepath);
	String key = MigrateUtils.createS3RandomKey(urn) + ".zip";
	String objBase64 = MigrateUtils.calculateFileBase64(filepath, super.getIntegrityType());
	try (InputStream is = Files.newInputStream(filepath)) {
	    return super.s3PutObjectAsFile(is, Files.size(filepath), objBase64, bucketName, key);
	}
    }

    /**
     * Crea un file ZIP contenente i file XML.
     *
     * @param xmlFiles Mappa dei file XML da includere.
     * @param zipFile  Percorso del file ZIP da creare.
     *
     * @throws IOException In caso di errore durante la creazione del file ZIP.
     */
    private void createZipFile(Map<String, String> xmlFiles, Path zipFile) throws IOException {
	try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zipFile))) {
	    for (Map.Entry<String, String> entry : xmlFiles.entrySet()) {
		out.putNextEntry(new ZipEntry(entry.getKey() + ".xml"));
		out.write(entry.getValue().getBytes());
		out.closeEntry();
	    }
	}
    }
}
