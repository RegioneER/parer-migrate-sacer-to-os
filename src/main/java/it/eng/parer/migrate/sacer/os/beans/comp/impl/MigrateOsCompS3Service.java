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

package it.eng.parer.migrate.sacer.os.beans.comp.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_COMP_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_DOC_COMP_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_DOC_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_DOC_PREFIX;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_PAD5DIGITS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.POSIX_STD_ATTR;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.normalizingKey;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.comp.IMigrateOsCompS3Service;
import it.eng.parer.migrate.sacer.os.beans.comp.ISacerCompDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroCompDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroContenutoComp;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsCompS3Service extends MigrateOsS3Abstract implements IMigrateOsCompS3Service {

    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10 megabyte

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.comp.bucket.name")
    String bucketName;

    @Inject
    ISacerCompDao sacerCompDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idCompDoc, Boolean delete)
	    throws AppMigrateOsS3Exception {
	// get AroCompDoc
	AroCompDoc compDoc = sacerCompDao.findCompDocById(idCompDoc);
	// get AroContenutoComp
	AroContenutoComp contenutoComp = compDoc.getAroContenutoComps().get(0);
	// get VrsSessioneVers
	VrsSessioneVers vrsSessioneVers = sacerCompDao
		.findVrsSessioneVersByIdStrutAndIdCompDoc(compDoc.getIdStrut(), idCompDoc);

	// 4. do migrate (new transaction per single object)
	// 4.1 create object
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	IObjectStorageResource osResource = null;
	try {
	    String tmpUrn = calculateUrnComponente(vrsSessioneVers, compDoc);
	    osResource = createResourcesCompDoc(tmpUrn, contenutoComp.getBlContenComp(),
		    compDoc.getIdCompDoc(), idSacerBackend);
	    // delete contenuto
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		Long idContenutoComp = contenutoComp.getIdContenComp();
		sacerCompDao.deleteBlContenutoComp(idContenutoComp);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message("Errore nella fase di migrazione componente id {0,number,#}",
			    idCompDoc)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message("Errore nella fase di migrazione componente id {0,number,#}",
			    idCompDoc)
		    .build();
	}
    }

    private static String formattaUrnPartDocumento(String categoria, int progressivo, boolean pgpad,
	    String fmtUsed, String padfmtUsed) {
	return MessageFormat.format(fmtUsed, categoria,
		pgpad ? String.format(padfmtUsed, progressivo) : progressivo);
    }

    private static String formattaUrnPartComponente(String urnBase, long ordinePresentazione,
	    String fmtUsed, String padfmtUsed) {
	return MessageFormat.format(fmtUsed, urnBase,
		String.format(padfmtUsed, ordinePresentazione));
    }

    /*
     * Calcolo dell'URN componente normalizzato
     */
    private String calculateUrnComponente(VrsSessioneVers sessioneVers, AroCompDoc compDoc)
	    throws AppMigrateOsS3Exception {
	//
	String tmpUrnDoc = null;

	// calculate normalized URN
	int progDoc = Objects.isNull(compDoc.getAroStrutDoc().getAroDoc().getNiOrdDoc())
		? compDoc.getAroStrutDoc().getAroDoc().getPgDoc().intValue()
		: compDoc.getAroStrutDoc().getAroDoc().getNiOrdDoc().intValue();
	String tmpUrnPartDoc = formattaUrnPartDocumento(S3_KEY_DOC_PREFIX, progDoc, true,
		S3_KEY_DOC_FMT, S3_KEY_PAD5DIGITS_FMT);

	if (compDoc.getAroCompDoc() != null) {
	    // E' UN SOTTOCOMPONENTE
	    // DOCXXXXX:NNNNN
	    tmpUrnDoc = formattaUrnPartComponente(tmpUrnPartDoc,
		    compDoc.getAroCompDoc().getNiOrdCompDoc().intValue(), S3_KEY_DOC_COMP_FMT,
		    S3_KEY_PAD5DIGITS_FMT);
	    // DOCXXXXX:NNNNN:KK
	    tmpUrnDoc = formattaUrnPartComponente(tmpUrnDoc, compDoc.getNiOrdCompDoc().intValue(),
		    S3_KEY_DOC_COMP_FMT, S3_KEY_PAD5DIGITS_FMT);
	} else {
	    // DOCXXXXX:NNNNN
	    tmpUrnDoc = formattaUrnPartComponente(tmpUrnPartDoc,
		    compDoc.getNiOrdCompDoc().intValue(), S3_KEY_DOC_COMP_FMT,
		    S3_KEY_PAD5DIGITS_FMT);
	}

	return calculateBaseUrn(sessioneVers.getNmEnte(), sessioneVers.getNmStrut(),
		sessioneVers.getCdRegistroKeyUnitaDoc(), sessioneVers.getAaKeyUnitaDoc().toString(),
		sessioneVers.getCdKeyUnitaDoc(), tmpUrnDoc, S3_KEY_COMP_FMT);
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
	    final String cdKeyUnitaDoc, String urnDoc, String fmt) throws AppMigrateOsS3Exception {
	try {
	    // base UD URN
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut));

	    final String urn_ud = MessageFormat.format(S3_KEY_UD_FMT,
		    normalizingKey(cdRegistroKeyUnitaDoc), aaKeyUnitaDoc,
		    normalizingKey(cdKeyUnitaDoc));

	    return formattaBaseUrnDoc(urn_versatore, urn_ud, urnDoc, fmt);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN con formato {0} e valori nmEnte {1} / nmStrut {2} / cdRegistroKeyUnitaDoc {3} / "
				    + "aaKeyUnitaDoc {4} / cdKeyUnitaDoc {5} / idDoc {6} / idCompDoc {7}",
			    fmt, nmEnte, nmStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
			    cdKeyUnitaDoc)
		    .build();
	}

    }

    private static String formattaBaseUrnDoc(String versatore, String unitaDoc, String documento,
	    String fmtUsed) {
	return MessageFormat.format(fmtUsed, versatore, unitaDoc, documento);
    }

    private IObjectStorageResource createResourcesCompDoc(final String urn, Blob blob,
	    Long idCompDoc, Long idBackend) throws IOException, AppMigrateOsS3Exception {
	// create tmp file
	Path filepath = Files.createTempFile("comp-", "", POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createCompAndPutOnBucket(urn, filepath, blob);
	    // link
	    sacerCompDao.saveObjectStorageLinkComp(osresource.getTenant(), osresource.getS3Bucket(),
		    osresource.getS3Key(), idCompDoc, idBackend);

	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message("Errore nella fase creazione risorsa per componente {0,number,#}",
			    idCompDoc)
		    .build();
	} finally {
	    Files.deleteIfExists(filepath);
	}
    }

    private IObjectStorageResource createCompAndPutOnBucket(final String urn, Path filepath,
	    Blob blob) throws IOException, NoSuchAlgorithmException, SQLException {
	// create key
	final String key = MigrateUtils.createS3RandomKey(urn);
	// create file
	createFile(blob, filepath);
	// sha256
	final String objbase64 = MigrateUtils.calculateFileBase64(filepath,
		super.getIntegrityType());
	// put object
	try (InputStream is = Files.newInputStream(filepath)) {
	    return super.s3PutObjectAsFile(is, Files.size(filepath), objbase64, bucketName, key);
	}
    }

    private void createFile(Blob blob, Path filepath) throws IOException, SQLException {
	try (BufferedOutputStream out = new BufferedOutputStream(
		new FileOutputStream(filepath.toFile()), BUFFER_SIZE);
		BufferedInputStream is = new BufferedInputStream(blob.getBinaryStream(),
			BUFFER_SIZE)) {
	    is.transferTo(out);
	}
    }

}
