package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.RICHIESTA;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.RISPOSTA;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_PAD5DIGITS_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_UD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_URN_UPD_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_VERSATORE_FMT;
import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.normalizingKey;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsS3Abstract;
import it.eng.parer.migrate.sacer.os.base.utils.Costants;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.IMigrateOsSipAggMetadatiS3Service;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.ISacerSipAggMetadatiDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUpdUnitaDoc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsSipAggMetadatiS3Service extends MigrateOsS3Abstract
	implements IMigrateOsSipAggMetadatiS3Service {
    private static final String MSG_TEMPLATE = "XML di tipo = {0} non presente o XML vuoto, non possibile effettuare migrazione per sip upd ud con id = {1}";

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName;

    @Inject
    ISacerSipAggMetadatiDao sacerSipDao;

    @Inject
    IMigrateSacerDao sacerDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppMigrateOsS3Exception.class })
    public IObjectStorageResource doMigrate(Long idSacerBackend, Long idUpdUnitaDoc,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	// get AroUpdUnitaDoc
	AroUpdUnitaDoc aroUpdUnitaDoc = sacerSipDao.findAroUpdUnitaDocById(idUpdUnitaDoc);
	// get AroXmlUpdUnitaDoc
	List<AroXmlUpdUnitaDoc> xmlUpdUnitaDoc = sacerSipDao
		.findAllXmlUpdUnitaDocByIdUpdUnitaDoc(idUpdUnitaDoc);
	// create Map
	Map<String, String> sipBlob = createUpdSipBlob(xmlUpdUnitaDoc, aroUpdUnitaDoc);

	IObjectStorageResource osResource = null;

	// 4. do migrate (new transaction per single object UPD)
	// 4.1 create sip.zp
	// 4.2 calculate base64 -> update on table (bucket+key+base64)
	// 4.3 migrate (S3)
	try {
	    // get AroUnitaDoc
	    AroUnitaDoc aroUnitaDoc = aroUpdUnitaDoc.getAroUnitaDoc();
	    // calculate urn
	    final String urn = calculateUrnSipUpdUd(aroUnitaDoc, aroUpdUnitaDoc.getPgUpdUnitaDoc());
	    osResource = createResourcesInSipUnitaUdAggMd(urn, sipBlob,
		    aroUpdUnitaDoc.getIdUpdUnitaDoc(), aroUpdUnitaDoc.getIdStrut(), idSacerBackend);

	    // delete XMLs
	    if (!Objects.isNull(deleteSrc) && deleteSrc.booleanValue()) {
		List<Long> xmlUdpUnitaDocIds = xmlUpdUnitaDoc.stream()
			.map(AroXmlUpdUnitaDoc::getIdXmlUpdUnitaDoc).toList();
		sacerSipDao.deleteBlXmlOnAroXmlUpdUnitaDoc(xmlUdpUnitaDocIds);
	    }

	    return osResource;
	} catch (IOException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase di migrazione (AroUpdUnitaDoc) aggiornamento metadati id {0,number,#}",
			    idUpdUnitaDoc)
		    .build();
	} catch (AppMigrateOsDeleteSrcException e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR).cause(e)
		    .osresource(osResource)
		    .message(
			    "Errore nella fase di migrazione (AroUpdUnitaDoc) aggiornamento metadati id {0,number,#}",
			    idUpdUnitaDoc)
		    .build();
	}
    }

    /**
     * Calcolo dell'URN unità documentaria normalizzato
     *
     * @param aroUnitaDoc   sessione di versamento
     *
     * @param pgUpdUnitaDoc progressivo aggiornamento unità documentaria
     *
     * @return URN normalizzato (utile come parte della chiave generata su O.S.)
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    private String calculateUrnSipUpdUd(AroUnitaDoc aroUnitaDoc, BigDecimal pgUpdUnitaDoc)
	    throws AppMigrateOsS3Exception {
	int idx = 0;
	// get nmEnte / nmStrut
	Object[] result = sacerDao.findNmEnteAndNmStrutByIdStrut(aroUnitaDoc.getIdStrut());
	// base UD URN
	return calculateBaseUrn((String) result[idx], (String) result[++idx],
		aroUnitaDoc.getCdRegistroKeyUnitaDoc(), aroUnitaDoc.getAaKeyUnitaDoc().toString(),
		aroUnitaDoc.getCdKeyUnitaDoc(), pgUpdUnitaDoc, S3_KEY_URN_UPD_FMT);
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
	    final String cdKeyUnitaDoc, BigDecimal pgUpdUnitaDoc, String fmt)
	    throws AppMigrateOsS3Exception {
	try {
	    // base UD URN
	    final String urn_versatore = MessageFormat.format(S3_KEY_VERSATORE_FMT,
		    normalizingKey(nmEnte), normalizingKey(nmStrut));

	    final String urn_ud = MessageFormat.format(S3_KEY_UD_FMT,
		    normalizingKey(cdRegistroKeyUnitaDoc), aaKeyUnitaDoc,
		    normalizingKey(cdKeyUnitaDoc));

	    final String urn_sipUpdUd = String.format(S3_KEY_PAD5DIGITS_FMT,
		    pgUpdUnitaDoc.intValue());

	    return MessageFormat.format(fmt, urn_versatore, urn_ud, urn_sipUpdUd);
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
    private Map<String, String> createUpdSipBlob(List<AroXmlUpdUnitaDoc> xmlUpdUnitaUd,
	    AroUpdUnitaDoc aroUpdUnitaDoc) throws AppMigrateOsS3Exception {
	Map<String, String> sipBlob = new HashMap<>();
	// mandatory
	Optional<AroXmlUpdUnitaDoc> richiesta = xmlUpdUnitaUd.stream()
		.filter(x -> x.getTiXmlUpdUnitaDoc().equals(RICHIESTA)).findFirst();
	if (richiesta.isPresent() && StringUtils.isNotEmpty(richiesta.get().getBlXml())) {
	    sipBlob.put(RICHIESTA, richiesta.get().getBlXml());
	} else {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR)
		    .message(MSG_TEMPLATE, RICHIESTA,
			    String.valueOf(aroUpdUnitaDoc.getIdUpdUnitaDoc()))
		    .build();
	}
	// mandatory
	Optional<AroXmlUpdUnitaDoc> risposta = xmlUpdUnitaUd.stream()
		.filter(x -> x.getTiXmlUpdUnitaDoc().equals(RISPOSTA)).findFirst();
	if (risposta.isPresent() && StringUtils.isNotEmpty(risposta.get().getBlXml())) {
	    sipBlob.put(RISPOSTA, risposta.get().getBlXml());
	} else {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.INTERNAL_ERROR)
		    .message(MSG_TEMPLATE, RISPOSTA,
			    String.valueOf(aroUpdUnitaDoc.getIdUpdUnitaDoc()))
		    .build();
	}

	return sipBlob;
    }

    private IObjectStorageResource createResourcesInSipUnitaUdAggMd(final String urn,
	    Map<String, String> xmlFiles, long idUpdUnitaDoc, long idStrut, Long idSacerBackend)
	    throws IOException, AppMigrateOsS3Exception {
	// create tmp file
	Path tempZip = Files.createTempFile("sip-", ".zip", MigrateUtils.POSIX_STD_ATTR);

	try {
	    IObjectStorageResource osresource = createSipXmlMapAndPutOnBucket(urn, tempZip,
		    xmlFiles);
	    // link
	    sacerSipDao.saveObjectStorageLinkSipUdAggMd(osresource.getTenant(),
		    osresource.getS3Bucket(), osresource.getS3Key(), idUpdUnitaDoc, idStrut,
		    idSacerBackend);

	    return osresource;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).category(ErrorCategory.S3_ERROR)
		    .cause(e)
		    .message(
			    "Errore nella fase creazione risorsa per sip upd unita documentaria {0,number,#}",
			    idUpdUnitaDoc)
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
