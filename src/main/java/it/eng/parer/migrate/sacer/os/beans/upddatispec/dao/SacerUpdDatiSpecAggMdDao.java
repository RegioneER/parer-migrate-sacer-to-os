package it.eng.parer.migrate.sacer.os.beans.upddatispec.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.ISacerUpdDatiSpecAggMdDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdCompUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdDatiSpecUdObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdDatiSpecUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdDocUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUdObjectStorageCnts.TiEntitaAroUpdDatiSpecUdObjectStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerUpdDatiSpecAggMdDao implements ISacerUpdDatiSpecAggMdDao {
    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManager;

    private static final String QUERY_AND = " and ";

    @Override
    public Stream<Long> findIdsUpdDatiSpecAggMd(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select distinct upddatispec.aroUpdUnitaDoc.idUpdUnitaDoc ");
	    queryStr.append("from AroUpdDatiSpecUnitaDoc upddatispec ");
	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("upddatispec.aroUpdUnitaDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc")
			.append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("upddatispec.orgStrut.idStrut = :idStrut").append(QUERY_AND);
	    }
	    // fixed (exclude sip already migrated)
	    queryStr.append(
		    " not exists (select 1 from AroUpdDatiSpecUdObjectStorage udos where udos.aroUpdUnitaDoc.idUpdUnitaDoc = upddatispec.aroUpdUnitaDoc.idUpdUnitaDoc)");

	    //
	    TypedQuery<Long> query = entityManager.createQuery(queryStr.toString(), Long.class);
	    // optional row limits
	    if (!Objects.isNull(filter.getRowlimit())) {
		query.setMaxResults(filter.getRowlimit().intValue()); // max result
	    }
	    // TODO: check hint for optimizations
	    query.setHint(HibernateHints.HINT_READ_ONLY, true);
	    // query.setHint(HibernateHints.HINT_CACHEABLE, true);
	    // query.setHint(HibernateHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		query.setParameter("idUnitaDoc", filter.getIdUnitadoc());
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase ricerca dei dati specifici di aggiornamento metadati ud.")
		    .build();
	}
    }

    @Override
    public void saveObjectStorageLinkUpdDatiSpecAggMd(String tenant, String bucket, String key,
	    Long idStrut, DatiSpecLinkOsKeyMap datiSpec, Long idBackend) {
	AroUpdDatiSpecUdObjectStorage osLink = new AroUpdDatiSpecUdObjectStorage();

	switch (datiSpec.getTipiEntitaSacer()) {
	case "UPD_UNI_DOC":
	    AroUpdUnitaDoc aroUpdUnitaDoc = entityManager.find(AroUpdUnitaDoc.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroUpdUnitaDoc(aroUpdUnitaDoc);
	    break;
	case "UPD_DOC":
	    AroUpdDocUnitaDoc aroUpdDocUnitaDoc = entityManager.find(AroUpdDocUnitaDoc.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroUpdUnitaDoc(aroUpdDocUnitaDoc.getAroUpdUnitaDoc());
	    osLink.setAroUpdDocUnitaDoc(aroUpdDocUnitaDoc);
	    break;
	case "UPD_COMP":
	    AroUpdCompUnitaDoc aroUpdCompUnitaDoc = entityManager.find(AroUpdCompUnitaDoc.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroUpdUnitaDoc(aroUpdCompUnitaDoc.getAroUpdDocUnitaDoc().getAroUpdUnitaDoc());
	    osLink.setAroUpdDocUnitaDoc(aroUpdCompUnitaDoc.getAroUpdDocUnitaDoc());
	    osLink.setAroUpdCompUnitaDoc(aroUpdCompUnitaDoc);
	    break;
	default:
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).message(
		    "Errore nella fase di salvataggio link dati specifi aggiornamento unità documentaria, tipologia entità non riconosciuta: {0}",
		    datiSpec.getTipiEntitaSacer()).build();
	}

	osLink.setTiEntitaSacer(
		TiEntitaAroUpdDatiSpecUdObjectStorage.valueOf(datiSpec.getTipiEntitaSacer()));
	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);
	osLink.setIdDecBackend(idBackend);
	osLink.setIdStrut(BigDecimal.valueOf(idStrut));

	entityManager.persist(osLink);
	entityManager.flush();
    }

    @Override
    public List<AroUpdDatiSpecUnitaDoc> findUpdDatiSpecAggMdByIdUpdUnitaDoc(Long idUpdUnitaDoc)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select upddatispec ");
	    queryStr.append("from AroUpdDatiSpecUnitaDoc upddatispec ");
	    queryStr.append("where upddatispec.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc");

	    //
	    TypedQuery<AroUpdDatiSpecUnitaDoc> query = entityManager
		    .createQuery(queryStr.toString(), AroUpdDatiSpecUnitaDoc.class);
	    // TODO: check hint for optimizations
	    query.setHint(HibernateHints.HINT_READ_ONLY, true);
	    // query.setHint(HibernateHints.HINT_CACHEABLE, true);
	    // query.setHint(HibernateHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

	    if (Objects.nonNull(idUpdUnitaDoc)) {
		query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
	    }

	    return query.getResultList();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase ricerca dei dati specifici di aggiornamento metadati ud.")
		    .build();
	}
    }

    @Override
    public void deleteBlUpdDatiSpecAggMd(List<Long> updDatiSpecUnitaDocIds)
	    throws AppMigrateOsDeleteSrcException {
	try {
	    updDatiSpecUnitaDocIds.forEach(updDatiSpecUnitaDocId -> {
		AroUpdDatiSpecUnitaDoc aroUpdDatiSpecUnitaDoc = entityManager
			.find(AroUpdDatiSpecUnitaDoc.class, updDatiSpecUnitaDocId);
		aroUpdDatiSpecUnitaDoc.setBlXmlDatiSpec(null); // update to null
		entityManager.persist(aroUpdDatiSpecUnitaDoc);
	    });
	    entityManager.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento clob XML dei dati specifici di aggiornamento metadati")
		    .build();
	}
    }

}
