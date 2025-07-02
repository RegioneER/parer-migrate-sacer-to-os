package it.eng.parer.migrate.sacer.os.beans.elenchivers.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_INDICE_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_INDICE_FIRMATO_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_MARCA_INDICE_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_FIRMA_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_MARCA_FIRMA_ELENCO_VERS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.formatDateAsStr;
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
import it.eng.parer.migrate.sacer.os.beans.elenchivers.IMigrateOsIndiceElencoVersS3Service;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.ISacerElencoVersDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvElencoVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvFileElencoVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsIndiceElencoVersS3Service extends MigrateOsS3Abstract
	implements IMigrateOsIndiceElencoVersS3Service {

    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10 megabyte

    @ConfigProperty(name = "s3.indiceelv.bucket.name")
    String bucketName;

    @Inject
    ISacerElencoVersDao sacerElencoVersDao;

    @Inject
    IMigrateSacerDao sacerDao;

    @Override
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idFileElencoVers,
	    Boolean delete) throws AppMigrateOsS3Exception {
	IObjectStorageResource osResource = null;
	try {
	    ElvFileElencoVers elvFileElencoVers = sacerElencoVersDao
		    .findFileElencoVersByIdFileElencoVers(idFileElencoVers);

	    ElvElencoVers elvElencoVers = elvFileElencoVers.getElvElencoVers();

	    osResource = doMigrateFileIndiceElencoVers(idSacerBackend, elvFileElencoVers,
		    elvElencoVers);

	    // delete contenuto
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		sacerElencoVersDao.deleteBlFileElencoVers(idFileElencoVers);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .osresource(osResource)
		    .message(
			    "Errore nella fase di migrazione elvFileElencoVers con id {0,number,#}",
			    idFileElencoVers)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message("Errore nella fase di delete elvFileElencoVers con id {0,number,#}",
			    idFileElencoVers)
		    .build();
	}
    }

    private IObjectStorageResource doMigrateFileIndiceElencoVers(Long idSacerBackend,
	    ElvFileElencoVers elvFileElencoVers, ElvElencoVers elvElencoVers)
	    throws AppMigrateOsS3Exception, IOException {
	// Implement the logic for migrating a single file of the indice elenco versamento
	// Example implementation:
	String urn = calculateUrnFileIndiceElencoVers(elvElencoVers, elvFileElencoVers);
	return createResourcesIndiceElencoVers(urn, elvFileElencoVers, idSacerBackend);
    }

    private static String formattaUrnFileIndiceElencoVers(String fmtUsed, String dtCreazioneElenco,
	    String idElencoVers) {
	return MessageFormat.format(fmtUsed, dtCreazioneElenco, idElencoVers);
    }

    /*
     * Calcolo dell'URN dei file dell'elenco di versamento normalizzato
     */
    private String calculateUrnFileIndiceElencoVers(ElvElencoVers elvElencoVers,
	    ElvFileElencoVers elvFileElencoVers) throws AppMigrateOsS3Exception {
	int idx = 0;
	String format = "";
	switch (elvFileElencoVers.getTiFileElencoVers()) {
	case INDICE:
	    format = S3_KEY_URN_INDICE_ELENCO_VERS_FMT;
	    break;
	case FIRMA:
	    format = S3_KEY_URN_FIRMA_ELENCO_VERS_FMT;
	    break;
	case MARCA_INDICE:
	    format = S3_KEY_URN_MARCA_INDICE_ELENCO_VERS_FMT;
	    break;
	case INDICE_FIRMATO:
	    format = S3_KEY_URN_INDICE_FIRMATO_ELENCO_VERS_FMT;
	    break;
	case MARCA_FIRMA:
	    format = S3_KEY_URN_MARCA_FIRMA_ELENCO_VERS_FMT;
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Unsupported file type: " + elvFileElencoVers.getTiFileElencoVers());
	}
	String tmpUrn = formattaUrnFileIndiceElencoVers(format,
		formatDateAsStr(elvElencoVers.getDtCreazioneElenco()),
		elvElencoVers.getIdElencoVers().toString());
	Object[] result = sacerDao.findNmEnteAndNmStrutByIdStrut(elvElencoVers.getIdStrut());
	return calculateBaseUrn((String) result[idx], (String) result[++idx], tmpUrn,
		S3_KEY_ELENCO_VERS_FMT);
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
	    String urnIndiceElencoVers, String fmt) throws AppMigrateOsS3Exception {
	try {
	    // base UD URN
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut));
	    return formattaBaseUrnIndiceElencoVers(urn_versatore, urnIndiceElencoVers, fmt);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN con formato {0} e valori nmEnte {1} / nmStrut {3}",
			    fmt, nmEnte, nmStrut)
		    .build();
	}

    }

    private static String formattaBaseUrnIndiceElencoVers(String versatore, String indiceElencoVers,
	    String fmtUsed) {
	return MessageFormat.format(fmtUsed, versatore, indiceElencoVers);
    }

    private IObjectStorageResource createResourcesIndiceElencoVers(final String urn,
	    ElvFileElencoVers elvFileElencoVers, Long idSacerBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// create tmp file
	Path filepath = Files.createTempFile("elvaip-", "", MigrateUtils.POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createIndiceElencoVersAndPutOnBucket(urn, filepath,
		    elvFileElencoVers);
	    // link
	    sacerElencoVersDao.saveObjectStorageLinkFileElencoVersUd(osresource.getTenant(),
		    osresource.getS3Bucket(), osresource.getS3Key(),
		    elvFileElencoVers.getIdFileElencoVers(), idSacerBackend);

	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per elenco indici aip {0,number,#}",
			    elvFileElencoVers.getIdFileElencoVers())
		    .build();
	} finally {
	    Files.deleteIfExists(filepath);
	}
    }

    private IObjectStorageResource createIndiceElencoVersAndPutOnBucket(final String urn,
	    Path filepath, ElvFileElencoVers elvFileElencoVers)
	    throws IOException, NoSuchAlgorithmException, SQLException {
	// create key
	String estensione = "";
	switch (elvFileElencoVers.getTiFileElencoVers()) {
	case INDICE:
	    estensione = ".xml";
	    break;
	case FIRMA:
	    estensione = ".tsr.p7m";
	    break;
	case MARCA_INDICE:
	    estensione = ".tsr";
	    break;
	case INDICE_FIRMATO:
	    estensione = ".xml.p7m";
	    break;
	case MARCA_FIRMA:
	    estensione = ".tsr";
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Unsupported file type: " + elvFileElencoVers.getTiFileElencoVers());
	}
	final String key = MigrateUtils.createS3RandomKey(urn) + estensione;
	// create file
	createFile(elvFileElencoVers.getBlFileElencoVers(), filepath);
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
