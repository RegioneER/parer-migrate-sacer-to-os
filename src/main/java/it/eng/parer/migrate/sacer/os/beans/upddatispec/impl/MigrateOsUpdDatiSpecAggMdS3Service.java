package it.eng.parer.migrate.sacer.os.beans.upddatispec.impl;

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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.ISacerSipAggMetadatiDao;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.IMigrateOsUpdDatiSpecAggMdS3Service;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.ISacerUpdDatiSpecAggMdDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdDatiSpecUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUnitaDocCnts.TiEntitaAroUpdDatiSpecUnitaDoc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsUpdDatiSpecAggMdS3Service extends MigrateOsS3Abstract
	implements IMigrateOsUpdDatiSpecAggMdS3Service {

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName;

    @Inject
    ISacerUpdDatiSpecAggMdDao sacerUpdDatiSpecAggMdDao;

    @Inject
    ISacerSipAggMetadatiDao sacerSipAggMetaDao;

    @Inject
    IMigrateSacerDao sacerDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idUpdUnitaDoc, Boolean delete)
	    throws AppMigrateOsS3Exception {
	// 1. get AroUpdUnitaDoc
	AroUpdUnitaDoc updUnitaDoc = sacerSipAggMetaDao.findAroUpdUnitaDocById(idUpdUnitaDoc);
	// 2. get AroUnitaDoc
	List<AroUpdDatiSpecUnitaDoc> datiSpec = updUnitaDoc.getAroUpdDatiSpecUnitaDocs();

	// 4. do migrate (new transaction per single object)
	// 4.1 create object
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	IObjectStorageResource osResource = null;

	try {
	    // 1. create object
	    Map<DatiSpecLinkOsKeyMap, Map<String, String>> xmlBlob = createUpdDatiSpecAggMdBlob(
		    datiSpec);
	    // 2. calculate base64 -> update on table (bucket+key+base64)
	    String tmpUrn = calculateUrnUpdDatiSpecAggMd(updUnitaDoc);

	    for (Map.Entry<DatiSpecLinkOsKeyMap, Map<String, String>> versIniDatiSpecBlobEntry : xmlBlob
		    .entrySet()) {
		osResource = createResourcesUpdDatiSpecUd(tmpUrn, versIniDatiSpecBlobEntry,
			updUnitaDoc.getIdStrut(), idSacerBackend);
	    }

	    // delete XMLs
	    if (!Objects.isNull(delete) && delete.booleanValue()) {
		List<Long> udpDatiSpecUnitaDocIds = datiSpec.stream()
			.map(AroUpdDatiSpecUnitaDoc::getIdUpdDatiSpecUnitaDoc).toList();
		sacerUpdDatiSpecAggMdDao.deleteBlUpdDatiSpecAggMd(udpDatiSpecUnitaDocIds);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore nella fase di migrazione dei dati specifici dell'aggiornamento ud con idUpdUnitaDoc {0,number,#}",
			    idUpdUnitaDoc)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message(
			    "Errore nella fase di migrazione dei dati specifici dell'aggiornamento ud con idUpdUnitaDoc {0,number,#}",
			    idUpdUnitaDoc)
		    .build();
	}
    }

    private Map<DatiSpecLinkOsKeyMap, Map<String, String>> createUpdDatiSpecAggMdBlob(
	    List<AroUpdDatiSpecUnitaDoc> datiSpecList) throws AppMigrateOsS3Exception {

	Map<DatiSpecLinkOsKeyMap, Map<String, String>> versIniDatiSpecBlob = new HashMap<>();

	try {
	    datiSpecList.forEach(datiSpec -> {
		switch (datiSpec.getTiEntitaSacer()) {
		case UPD_UNI_DOC:
		    DatiSpecLinkOsKeyMap keyUd = new DatiSpecLinkOsKeyMap(
			    datiSpec.getAroUpdUnitaDoc().getIdUpdUnitaDoc(),
			    TiEntitaAroUpdDatiSpecUnitaDoc.UPD_UNI_DOC.name());
		    if (versIniDatiSpecBlob.containsKey(keyUd)) {
			versIniDatiSpecBlob.get(keyUd).put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
		    } else {
			Map<String, String> versIniDatiSpecUDBlob = new HashMap<>();
			versIniDatiSpecUDBlob.put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
			versIniDatiSpecBlob.put(keyUd, versIniDatiSpecUDBlob);
		    }
		    break;
		case UPD_DOC:
		    DatiSpecLinkOsKeyMap keyDoc = new DatiSpecLinkOsKeyMap(
			    datiSpec.getIdUpdDocUnitaDoc(),
			    TiEntitaAroUpdDatiSpecUnitaDoc.UPD_DOC.name());
		    if (versIniDatiSpecBlob.containsKey(keyDoc)) {
			versIniDatiSpecBlob.get(keyDoc).put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
		    } else {
			Map<String, String> versIniDatiSpecDocBlob = new HashMap<>();
			versIniDatiSpecDocBlob.put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
			versIniDatiSpecBlob.put(keyDoc, versIniDatiSpecDocBlob);
		    }
		    break;
		case UPD_COMP:
		    DatiSpecLinkOsKeyMap keyComp = new DatiSpecLinkOsKeyMap(
			    datiSpec.getIdUpdCompUnitaDoc(),
			    TiEntitaAroUpdDatiSpecUnitaDoc.UPD_COMP.name());
		    if (versIniDatiSpecBlob.containsKey(keyComp)) {
			versIniDatiSpecBlob.get(keyComp).put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
		    } else {
			Map<String, String> versIniDatiSpecCompBlob = new HashMap<>();
			versIniDatiSpecCompBlob.put(datiSpec.getTiUsoXsd().name(),
				datiSpec.getBlXmlDatiSpec());
			versIniDatiSpecBlob.put(keyComp, versIniDatiSpecCompBlob);
		    }
		    break;
		}

	    });

	    return versIniDatiSpecBlob;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante creazione mappa blob dati specifici agggiornamento unità documentaria")
		    .build();
	}
    }

    /*
     * Calcolo dell'URN normalizzato
     */
    private String calculateUrnUpdDatiSpecAggMd(AroUpdUnitaDoc updUnitaDoc)
	    throws AppMigrateOsS3Exception {
	//
	int idx = 0;
	try {
	    Object[] result = sacerDao.findNmEnteAndNmStrutByIdStrut(updUnitaDoc.getIdStrut());

	    return calculateBaseUrn(
		    formattaUrnPartVersatoreKeyOs((String) result[idx], (String) result[++idx]),
		    formattaUrnPartUnitaDocKeyOs(updUnitaDoc.getAroUnitaDoc()),
		    updUnitaDoc.getPgUpdUnitaDoc().longValue(), true, S3_KEY_PAD5DIGITS_FMT);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.S3_ERROR).cause(e)
		    .message(
			    "Errore durante calcolo URN dati specifici aggiornamento unità documentaria, idUpdUnitaDoc {0,number,#}",
			    updUnitaDoc.getIdUpdUnitaDoc())
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

    private IObjectStorageResource createResourcesUpdDatiSpecUd(final String urn,
	    Map.Entry<DatiSpecLinkOsKeyMap, Map<String, String>> xmlBlob, Long idStrut,
	    Long idBackend) throws IOException, AppMigrateOsS3Exception {
	// create tmp file
	Path filepath = Files.createTempFile("dati_spec-", ".zip", MigrateUtils.POSIX_STD_ATTR);
	try {
	    IObjectStorageResource osresource = createUpdDatiSpecAndPutOnBucket(urn, filepath,
		    xmlBlob.getValue());
	    // link
	    sacerUpdDatiSpecAggMdDao.saveObjectStorageLinkUpdDatiSpecAggMd(osresource.getTenant(),
		    osresource.getS3Bucket(), osresource.getS3Key(), idStrut, xmlBlob.getKey(),
		    idBackend);

	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per dati specifici {0,number,#}, tipo entità {}",
			    xmlBlob.getKey().getIdEntitySacer(),
			    xmlBlob.getKey().getTipiEntitaSacer())
		    .build();
	} finally {
	    Files.deleteIfExists(filepath);
	}
    }

    private IObjectStorageResource createUpdDatiSpecAndPutOnBucket(final String urn, Path filepath,
	    Map<String, String> xmlBlob)
	    throws IOException, NoSuchAlgorithmException {
	// create key
	final String key = MigrateUtils.createS3RandomKey(urn) + ".zip";
	// create file
	createZipFile(xmlBlob, filepath);
	// sha256
	final String objbase64 = MigrateUtils.calculateFileBase64(filepath,
		super.getIntegrityType());
	// put object
	try (InputStream is = Files.newInputStream(filepath)) {
	    return super.s3PutObjectAsFile(is, Files.size(filepath), objbase64, bucketName, key);
	}
    }

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
