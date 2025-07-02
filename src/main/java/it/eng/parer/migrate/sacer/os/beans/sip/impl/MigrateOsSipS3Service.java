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

package it.eng.parer.migrate.sacer.os.beans.sip.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.INDICE_FILE;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.RAPP_VERS;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.RICHIESTA;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.RISPOSTA;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_DOC_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.AGGIUNGI_DOCUMENTO;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.VERSAMENTO;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.POSIX_STD_ATTR;
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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.utils.Costants;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.sip.IMigrateOsSipS3Service;
import it.eng.parer.migrate.sacer.os.beans.sip.ISacerSipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsXmlDatiSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsSipS3Service extends MigrateOsS3Abstract implements IMigrateOsSipS3Service {

    private static final String MSG_TEMPLATE = "XML di tipo = {0} non presente o XML vuoto, non possibile effettuare migrazione per sessione di versamento id = {1} di tipo = {2}";

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.sip.bucket.name")
    String bucketName;

    @Inject
    ISacerSipDao sacerSipDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idSessioneVers,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	// get VrsSessioneVers
	VrsSessioneVers sessioneVers = sacerSipDao.findVrsSessioneVersById(idSessioneVers);
	// get VrsXmlDatiSessioneVers
	List<VrsXmlDatiSessioneVers> xmlDatiSessioneVerss = sacerSipDao
		.findAllXmlDatiSessioneVersByIdSessVers(idSessioneVers);
	// create Map
	Map<String, String> sipBlob = createSipBlob(xmlDatiSessioneVerss, sessioneVers);
	// 4. do migrate (new transaction per single object)
	// 4.1 create object
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	IObjectStorageResource osResource = null;
	try {

	    if (sessioneVers.getTiSessioneVers().equals(VERSAMENTO.name())) {
		//
		// calculate normalized URN
		final String urn = calculateUrnUD(sessioneVers);
		osResource = createResourcesInSipUnitaDoc(urn, sipBlob,
			sessioneVers.getAroUnitaDoc().getIdUnitaDoc(), idSacerBackend);
	    } else if (sessioneVers.getTiSessioneVers().equals(AGGIUNGI_DOCUMENTO.name())) {
		//
		// calculate normalized URN
		final String urn = calculateUrnDOC(sessioneVers);
		osResource = createResourcesInSipDocumento(urn, sipBlob,
			sessioneVers.getAroDoc().getIdDoc(), idSacerBackend);
	    } else {
		throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).message(
			"Migrazione non possibile per sessione di versamento id {0} di tipo {1}",
			String.valueOf(sessioneVers.getIdSessioneVers()),
			sessioneVers.getTiSessioneVers()).build();
	    }
	    // delete XMLs
	    if (!Objects.isNull(deleteSrc) && deleteSrc.booleanValue()) {
		List<Long> xmlDatiSessioneVersIds = xmlDatiSessioneVerss.stream()
			.map(VrsXmlDatiSessioneVers::getIdXmlDatiSessioneVers).toList();
		sacerSipDao.deleteBlXmlOnVrsXmlDatiSessioneVers(xmlDatiSessioneVersIds);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase di migrazione sessione di versamento id {0,number,#}",
			    sessioneVers.getIdSessioneVers())
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e).osresource(osResource)
		    .message(
			    "Errore nella fase di migrazione sessione di versamento id {0,number,#}",
			    sessioneVers.getIdSessioneVers())
		    .build();
	}
    }

    /**
     * Calcolo dell'URN unità documentaria normalizzato
     *
     * @param sessioneVers sessione di versamento
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateUrnUD(VrsSessioneVers sessioneVers) throws AppMigrateOsS3Exception {
	// base UD URN
	return calculateBaseUrn(sessioneVers.getNmEnte(), sessioneVers.getNmStrut(),
		sessioneVers.getCdRegistroKeyUnitaDoc(), sessioneVers.getAaKeyUnitaDoc().toString(),
		sessioneVers.getCdKeyUnitaDoc(), S3_KEY_URN_UD_FMT);
    }

    /**
     * Calcolo dell'URN DOC normalizzato
     *
     * @param sessioneVers sessione di versamento
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateUrnDOC(VrsSessioneVers sessioneVers) throws AppMigrateOsS3Exception {
	// base DOC URN
	return calculateBaseUrn(sessioneVers.getNmEnte(), sessioneVers.getNmStrut(),
		sessioneVers.getCdRegistroKeyUnitaDoc(), sessioneVers.getAaKeyUnitaDoc().toString(),
		sessioneVers.getCdKeyUnitaDoc(), S3_KEY_URN_DOC_FMT);
    }

    /**
     * Calcolo dell'URN unità documentaria normalizzato
     *
     * @param sessioneVers sessione di versamento
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateBaseUrn(final String nmEnte, final String nmStrut,
	    final String cdRegistroKeyUnitaDoc, final String aaKeyUnitaDoc,
	    final String cdKeyUnitaDoc, String fmt) throws AppMigrateOsS3Exception {
	try {
	    // base UD URN
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut));

	    final String urn_ud = MessageFormat.format(S3_KEY_UD_FMT,
		    normalizingKey(cdRegistroKeyUnitaDoc), aaKeyUnitaDoc,
		    normalizingKey(cdKeyUnitaDoc));

	    return MessageFormat.format(fmt, urn_versatore, urn_ud);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN con formato {0} e valori nmEnte {1} / nmStrut {2} / cdRegistroKeyUnitaDoc {3} / "
				    + "aaKeyUnitaDoc {4} / cdKeyUnitaDoc {5}",
			    fmt, nmEnte, nmStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
			    cdKeyUnitaDoc)
		    .build();
	}

    }

    /**
     * Creazione del blob (zip) da trasferire su bucket via S3
     *
     * @param xmlDatiSessioneVerss
     *
     * @param xmlDatiSessioneVerss xml associati alla sessione di versamento (SACER)
     * @param sessioneVers
     *
     * @return restituisce una mappa chiave=valore con i singoli XML
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private Map<String, String> createSipBlob(List<VrsXmlDatiSessioneVers> xmlDatiSessioneVerss,
	    VrsSessioneVers sessioneVers) throws AppMigrateOsS3Exception {
	Map<String, String> sipBlob = new HashMap<>();
	// mandatory
	Optional<VrsXmlDatiSessioneVers> richiesta = xmlDatiSessioneVerss.stream()
		.filter(x -> x.getTiXmlDati().equals(RICHIESTA)).findFirst();
	if (richiesta.isPresent() && StringUtils.isNotEmpty(richiesta.get().getBlXml())) {
	    sipBlob.put(RICHIESTA, richiesta.get().getBlXml());
	} else {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR)
		    .message(MSG_TEMPLATE, RICHIESTA,
			    String.valueOf(sessioneVers.getIdSessioneVers()),
			    sessioneVers.getTiSessioneVers())
		    .build();
	}
	// mandatory
	Optional<VrsXmlDatiSessioneVers> risposta = xmlDatiSessioneVerss.stream()
		.filter(x -> x.getTiXmlDati().equals(RISPOSTA)).findFirst();
	if (risposta.isPresent() && StringUtils.isNotEmpty(risposta.get().getBlXml())) {
	    sipBlob.put(RISPOSTA, risposta.get().getBlXml());
	} else {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR)
		    .message(MSG_TEMPLATE, RISPOSTA,
			    String.valueOf(sessioneVers.getIdSessioneVers()),
			    sessioneVers.getTiSessioneVers())
		    .build();
	}
	// not mandatory
	Optional<VrsXmlDatiSessioneVers> rdv = xmlDatiSessioneVerss.stream()
		.filter(x -> x.getTiXmlDati().equals(RAPP_VERS)).findFirst();
	if (rdv.isPresent() && StringUtils.isNotEmpty(rdv.get().getBlXml())) {
	    sipBlob.put(RAPP_VERS, rdv.get().getBlXml());
	}
	// not mandatory
	Optional<VrsXmlDatiSessioneVers> indicemm = xmlDatiSessioneVerss.stream()
		.filter(x -> x.getTiXmlDati().equals(INDICE_FILE)).findFirst();
	if (indicemm.isPresent() && StringUtils.isNotEmpty(indicemm.get().getBlXml())) {
	    sipBlob.put(INDICE_FILE, indicemm.get().getBlXml());
	}
	return sipBlob;
    }

    private IObjectStorageResource createResourcesInSipUnitaDoc(final String urn,
	    Map<String, String> xmlFiles, Long idUnitaDoc, Long idBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// create tmp file
	Path tempZip = Files.createTempFile("sip-", ".zip", POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createSipXmlMapAndPutOnBucket(urn, tempZip,
		    xmlFiles);
	    // link
	    sacerSipDao.saveObjectStorageLinkSipUd(osresource.getTenant(), osresource.getS3Bucket(),
		    osresource.getS3Key(), idUnitaDoc, idBackend);

	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per unita documentaria {0,number,#}",
			    idUnitaDoc)
		    .build();
	} finally {
	    Files.deleteIfExists(tempZip);
	}
    }

    private IObjectStorageResource createResourcesInSipDocumento(final String urn,
	    Map<String, String> xmlFiles, Long idDoc, Long idBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// put on O.S.
	Path tempZip = Files.createTempFile("sip-", ".zip", POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createSipXmlMapAndPutOnBucket(urn, tempZip,
		    xmlFiles);
	    // link
	    sacerSipDao.saveObjectStorageLinkSipDoc(osresource.getTenant(),
		    osresource.getS3Bucket(), osresource.getS3Key(), idDoc, idBackend);
	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message("Errore nella fase creazione risorsa per documento {0,number,#}",
			    idDoc)
		    .build();
	} finally {
	    Files.deleteIfExists(tempZip);
	}
    }

    /**
     * Creazione del file da inviare su bucket via S3
     *
     * @param tempZip  file temp
     * @param xmlFiles mappa XML (metadati)
     *
     * @return restituisce l'interfaccia con le coordinate dell'oggetto inviato su bucket
     *
     * @throws IOException              eccezione generica
     * @throws NoSuchAlgorithmException eccezione generica
     */
    private IObjectStorageResource createSipXmlMapAndPutOnBucket(final String urn, Path tempZip,
	    Map<String, String> xmlFiles) throws IOException, NoSuchAlgorithmException {
	// create key
	final String key = MigrateUtils.createS3RandomKey(urn) + ".zip";
	// create zip file
	createZipFile(xmlFiles, tempZip);
	// sha256
	final String objbase64 = MigrateUtils.calculateFileBase64(tempZip,
		super.getIntegrityType());
	// put object
	try (InputStream is = Files.newInputStream(tempZip)) {
	    return super.s3PutObjectAsFile(is, Files.size(tempZip), objbase64, bucketName, key);
	}
    }

    /**
     * Crea i file zip contenente i vari xml di versamento.Possono essere di tipo:
     * <ul>
     * <li>{@link Costants#RICHIESTA} obbligatorio è il sip di versamento</li>
     * <li>{@link Costants#RISPOSTA}, obbligatorio è la risposta del sip di versamento</li>
     * <li>{@link Costants#RAPP_VERS}, obbligatorio è il rapporto di versamento</li>
     * <li>{@link Costants#INDICE_FILE}, è presente solo nel caso di Versamento Multimedia</li>
     * </ul>
     *
     *
     * @param xmlFiles mappa dei file delle tipologie indicate in descrizione.
     * @param zipFile  file zip su cui salvare tutto
     *
     * @throws IOException in caso di errore
     */
    private void createZipFile(Map<String, String> xmlFiles, Path zipFile) throws IOException {
	try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zipFile))) {
	    for (Entry<String, String> sipBlob : xmlFiles.entrySet()) {
		ZipEntry entry = new ZipEntry(sipBlob.getKey() + ".xml");
		out.putNextEntry(entry);
		out.write(sipBlob.getValue().getBytes());
		out.closeEntry();
	    }
	}
    }

}
