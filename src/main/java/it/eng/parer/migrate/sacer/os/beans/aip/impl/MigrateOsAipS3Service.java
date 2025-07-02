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

package it.eng.parer.migrate.sacer.os.beans.aip.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_AIP_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_AIP_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.POSIX_STD_ATTR;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.normalizingKey;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.aip.IMigrateOsAipS3Service;
import it.eng.parer.migrate.sacer.os.beans.aip.ISacerAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroFileVerIndiceAipUd;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVerIndiceAipUd;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsAipS3Service extends MigrateOsS3Abstract implements IMigrateOsAipS3Service {

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.aip.bucket.name")
    String bucketName;

    @Inject
    ISacerAipDao sacerAipDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idVerIndiceAip,
	    Boolean delete) throws AppMigrateOsS3Exception {
	// get AroVerIndiceAipUd, AroFileVerIndiceAipUd, VrsSessioneVers
	Object[] result = sacerAipDao
		.findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(idVerIndiceAip);
	//
	int idx = 0;
	AroVerIndiceAipUd indiceAip = (AroVerIndiceAipUd) result[idx++];
	AroFileVerIndiceAipUd fileIndiceAip = (AroFileVerIndiceAipUd) result[idx++];
	AroUnitaDoc unitaDoc = (AroUnitaDoc) result[idx++];
	VrsSessioneVers sessioneVers = (VrsSessioneVers) result[idx];

	// 4. do migrate (new transaction per single object)
	// 4.1 create object
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	IObjectStorageResource osResource = null;
	try {
	    String tmpUrn = calculateUrnIndiceAip(indiceAip, sessioneVers);
	    osResource = createResourcesIndiceAip(tmpUrn, fileIndiceAip.getBlFileVerIndiceAip(),
		    unitaDoc.getIdSubStrut(), unitaDoc.getAaKeyUnitaDoc(),
		    indiceAip.getIdVerIndiceAip(), idSacerBackend);
	    // delete file ver indice aip
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		Long idFileVerIndiceAIp = fileIndiceAip.getIdFileVerIndiceAipUd();
		sacerAipDao.deleteBlFileVerIndiceAip(idFileVerIndiceAIp);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e).message("Errore nella fase di migrazione indice aip, id {0,number,#}",
			    idVerIndiceAip)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message("Errore nella fase di migrazione indice aip, id {0,number,#}",
			    idVerIndiceAip)
		    .build();
	}
    }

    /*
     * Calcolo dell'URN componente normalizzato
     */
    private String calculateUrnIndiceAip(AroVerIndiceAipUd indiceAip, VrsSessioneVers sessioneVers)
	    throws AppMigrateOsS3Exception {
	// calculate normalized URN

	return calculateBaseUrn(sessioneVers.getNmEnte(), sessioneVers.getNmStrut(),
		sessioneVers.getCdRegistroKeyUnitaDoc(), sessioneVers.getAaKeyUnitaDoc().toString(),
		sessioneVers.getCdKeyUnitaDoc(), sessioneVers.getAroUnitaDoc().getIdUnitaDoc(),
		indiceAip.getPgVerIndiceAip(), S3_KEY_AIP_FMT);
    }

    /**
     * Calcolo dell'URN indice aip normalizzato
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateBaseUrn(final String nmEnte, final String nmStrut,
	    final String cdRegistroKeyUnitaDoc, final String aaKeyUnitaDoc,
	    final String cdKeyUnitaDoc, final Long idUnitaDoc, BigDecimal pgVerIndiceAip,
	    String fmt) throws AppMigrateOsS3Exception {
	try {
	    // base URN versatore
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut));
	    // base URN UD
	    final String urn_ud = MessageFormat.format(S3_KEY_AIP_UD_FMT,
		    normalizingKey(cdRegistroKeyUnitaDoc), aaKeyUnitaDoc,
		    normalizingKey(cdKeyUnitaDoc), idUnitaDoc.toString());

	    return formattaBaseUrnAip(urn_versatore, urn_ud, pgVerIndiceAip, fmt);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN con formato {0} e valori nmEnte {1} / nmStrut {2} / cdRegistroKeyUnitaDoc {3} / "
				    + "aaKeyUnitaDoc {4} / idUnitaDoc {5} / pgVerIndiceAip {6}",
			    fmt, nmEnte, nmStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, idUnitaDoc,
			    pgVerIndiceAip)
		    .build();
	}
    }

    private static String formattaBaseUrnAip(String versatore, String unitaDoc,
	    BigDecimal pgVerIndiceAip, String fmtUsed) {
	return MessageFormat.format(fmtUsed, versatore, unitaDoc, pgVerIndiceAip);
    }

    private IObjectStorageResource createResourcesIndiceAip(final String urn, String blIndiceAip,
	    Long idSubStrut, BigDecimal aaKeyUnitaDoc, Long idVerIndiceAip, Long idSacerBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// put on O.S.
	Path tempFile = Files.createTempFile("aip-", "", POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createIndiceAipAndPutOnBucket(urn, tempFile,
		    blIndiceAip);
	    // link
	    sacerAipDao.saveObjectStorageLinkAip(osresource.getTenant(), osresource.getS3Bucket(),
		    osresource.getS3Key(), idSubStrut, aaKeyUnitaDoc, idVerIndiceAip,
		    idSacerBackend);
	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message("Errore nella fase creazione risorsa per indice aip {0,number,#}",
			    idVerIndiceAip)
		    .build();
	} finally {
	    Files.deleteIfExists(tempFile);
	}
    }

    /**
     * Creazione del file da inviare su bucket via S3
     *
     * @param tempFile file temp
     * @param xmlFiles mappa XML (metadati)
     *
     * @return restituisce l'interfaccia con le coordinate dell'oggetto inviato su bucket
     *
     * @throws IOException              eccezione generica
     * @throws NoSuchAlgorithmException eccezione generica
     */
    private IObjectStorageResource createIndiceAipAndPutOnBucket(final String urn, Path tempFile,
	    String blIndiceAip) throws IOException, NoSuchAlgorithmException {
	// create temp file
	createFile(blIndiceAip, tempFile);
	// sha256
	final String objbase64 = MigrateUtils.calculateFileBase64(tempFile,
		super.getIntegrityType());
	// put object
	try (InputStream is = Files.newInputStream(tempFile)) {
	    return super.s3PutObjectAsFile(is, Files.size(tempFile), objbase64, bucketName, urn);
	}
    }

    /**
     * Crea il file contenente l'xml dell'indice aip.
     *
     *
     * @param blIndiceAip aip string
     * @param filepath    filepath su cui salvare tutto
     *
     * @throws IOException in caso di errore
     */
    private void createFile(String blIndiceAip, Path filepath) throws IOException {
	Files.writeString(filepath, blIndiceAip, StandardCharsets.UTF_8);
    }

}
