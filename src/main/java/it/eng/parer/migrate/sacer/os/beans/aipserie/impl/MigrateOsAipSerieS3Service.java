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

package it.eng.parer.migrate.sacer.os.beans.aipserie.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_AIP_SERIE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_INDICE_AIP_SERIE_UD_FIR_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_INDICE_AIP_SERIE_UD_MARCA_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_AIP_SERIE_FMT;
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

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.aipserie.IMigrateOsAipSerieS3Service;
import it.eng.parer.migrate.sacer.os.beans.aipserie.ISacerAipSerieDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.SerFileVerSerie;
import it.eng.parer.migrate.sacer.os.jpa.sacer.SerSerie;
import it.eng.parer.migrate.sacer.os.jpa.sacer.SerVerSerie;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.SerFileVerSerieCnts.TiFileVerSerie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsAipSerieS3Service extends MigrateOsS3Abstract
	implements IMigrateOsAipSerieS3Service {

    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10 megabyte

    @ConfigProperty(name = "s3.aipserie.bucket.name")
    String bucketName;

    @Inject
    ISacerAipSerieDao sacerAipDao;

    @Inject
    IMigrateSacerDao sacerDao;

    @Override
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idFileVerSerie,
	    Boolean delete) throws AppMigrateOsS3Exception {
	// get AroVerIndiceAipUd, AroFileVerIndiceAipUd, VrsSessioneVers
	Object[] result = sacerAipDao
		.findSerSerieAndIndiceAndFileAipByIdFileVerSerie(idFileVerSerie);
	//
	int idx = 0;
	SerSerie serSerie = (SerSerie) result[idx++];
	SerVerSerie indiceAip = (SerVerSerie) result[idx++];
	SerFileVerSerie fileIndiceAip = (SerFileVerSerie) result[idx];

	// 4. do migrate (new transaction per single object)
	// 4.1 create object
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	IObjectStorageResource osResource = null;
	try {
	    String tmpUrn = calculateUrnIndiceAipSerie(serSerie, indiceAip, fileIndiceAip);
	    osResource = createResourcesIndiceAipSerie(tmpUrn, fileIndiceAip.getBlFile(),
		    fileIndiceAip.getIdStrut(), indiceAip.getIdVerSerie(),
		    fileIndiceAip.getTiFileVerSerie(), idSacerBackend);
	    // delete file ver indice aip
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		sacerAipDao.deleteBlFileVerSerie(idFileVerSerie);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase di migrazione indice aip serie, idFileVerSerie {0,number,#}",
			    idFileVerSerie)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message(
			    "Errore nella fase di migrazione indice aip serie, idFileVerSerie {0,number,#}",
			    idFileVerSerie)
		    .build();
	}
    }

    /*
     * Calcolo dell'URN componente normalizzato
     */
    private String calculateUrnIndiceAipSerie(SerSerie serSerie, SerVerSerie serVerSerie,
	    SerFileVerSerie serFileVerSerie) throws AppMigrateOsS3Exception {
	// calculate normalized URN
	String fmt = null;

	switch (serFileVerSerie.getTiFileVerSerie()) {
	case IX_AIP_UNISINCRO:
	    fmt = S3_KEY_URN_AIP_SERIE_FMT;
	    break;
	case IX_AIP_UNISINCRO_FIRMATO:
	    fmt = S3_KEY_URN_INDICE_AIP_SERIE_UD_FIR_FMT;
	    break;
	case MARCA_IX_AIP_UNISINCRO:
	    fmt = S3_KEY_URN_INDICE_AIP_SERIE_UD_MARCA_FMT;
	    break;
	default:
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).message(
		    "Errore durante calcolo formato URN, tipo serie non prevista {0} / id file serie {1,number,#}",
		    serFileVerSerie.getTiFileVerSerie(), serFileVerSerie.getIdFileVerSerie())
		    .build();
	}

	Object[] res = sacerDao.findNmEnteAndNmStrutByIdStrut(serFileVerSerie.getIdStrut());
	int idx = 0;

	return calculateBaseUrn((String) res[idx], (String) res[++idx],
		serSerie.getCdCompositoSerie(), serVerSerie.getCdVerSerie(), fmt);
    }

    /**
     * Calcolo dell'URN indice aip normalizzato
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateBaseUrn(final String nmEnte, final String nmStrut,
	    final String cdCompositoSerie, final String cdVerSerie, String fmt)
	    throws AppMigrateOsS3Exception {
	try {
	    // base URN versatore
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_AIP_SERIE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut), cdCompositoSerie);

	    return formattaBaseUrnAip(urn_versatore, cdVerSerie, fmt);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN con formato {0} e valori nmEnte {1} / nmStrut {2} / cdCompositoSerie {3} / "
				    + "cdVerSerie {4}",
			    fmt, nmEnte, nmStrut, cdCompositoSerie, cdVerSerie)
		    .build();
	}
    }

    private static String formattaBaseUrnAip(String versatore, String cdVerSerie, String fmtUsed) {
	return MessageFormat.format(fmtUsed, versatore, cdVerSerie);
    }

    private IObjectStorageResource createResourcesIndiceAipSerie(final String urn, Blob blIndiceAip,
	    Long idStrut, Long idVerSerie, TiFileVerSerie tiFileVerSerie, Long idSacerBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// put on O.S.
	Path tempFile = Files.createTempFile("aip-", "", POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createIndiceAipAndPutOnBucket(urn, tempFile,
		    blIndiceAip, tiFileVerSerie);
	    // link
	    sacerAipDao.saveObjectStorageLinkAipSerie(osresource.getTenant(),
		    osresource.getS3Bucket(), osresource.getS3Key(), idStrut, idVerSerie,
		    tiFileVerSerie, idSacerBackend);
	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per indice aip serie {0,number,#}",
			    idVerSerie)
		    .build();
	} finally {
	    Files.deleteIfExists(tempFile);
	}
    }

    /**
     *
     * Creazione del file da inviare su bucket via S3
     *
     * @param urn            oggetto calcolato
     * @param tempFile       file temporaneo
     * @param blIndiceAip    puntato al file da inviare
     * @param tiFileVerSerie tipo serie
     *
     * @return oggetto S3
     *
     * @throws IOException              eccezione generica I/O
     * @throws NoSuchAlgorithmException eccezione generica
     * @throws SQLException             eccezione generica
     */
    private IObjectStorageResource createIndiceAipAndPutOnBucket(final String urn, Path tempFile,
	    Blob blIndiceAip, TiFileVerSerie tiFileVerSerie)
	    throws IOException, NoSuchAlgorithmException, SQLException {
	String estensione = null;

	switch (tiFileVerSerie) {
	case IX_AIP_UNISINCRO:
	    estensione = ".xml";
	    break;
	case IX_AIP_UNISINCRO_FIRMATO:
	    estensione = ".xml.p7m";
	    break;
	case MARCA_IX_AIP_UNISINCRO:
	    estensione = ".tsr";
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Tipo di file non previsto per la creazione dell'oggetto su S3: "
			    + tiFileVerSerie);
	}
	final String key = MigrateUtils.createS3RandomKey(urn) + estensione;
	// create temp file
	createFile(blIndiceAip, tempFile);
	// sha256
	final String objbase64 = MigrateUtils.calculateFileBase64(tempFile,
		super.getIntegrityType());
	// put object
	try (InputStream is = Files.newInputStream(tempFile)) {
	    return super.s3PutObjectAsFile(is, Files.size(tempFile), objbase64, bucketName, key);
	}
    }

    /**
     * Crea il file contenente l'xml dell'indice aip.
     *
     *
     * @param blIndiceAip aip string
     * @param filepath    filepath su cui salvare tutto
     *
     * @throws IOException  in caso di errore
     * @throws SQLException
     */
    private void createFile(Blob blIndiceAip, Path filepath) throws IOException, SQLException {
	try (BufferedOutputStream out = new BufferedOutputStream(
		new FileOutputStream(filepath.toFile()), BUFFER_SIZE);
		BufferedInputStream is = new BufferedInputStream(blIndiceAip.getBinaryStream(),
			BUFFER_SIZE)) {
	    is.transferTo(out);
	}
    }

}
