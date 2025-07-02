package it.eng.parer.migrate.sacer.os.beans.comp.dao;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TiStatoSesioneVers.CHIUSA_OK;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.VERSAMENTO;

import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.comp.ISacerCompDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroCompDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroCompObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroContenutoComp;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerCompDao implements ISacerCompDao {
    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";

    @Override
    public Stream<Long> findIdsCompDoc(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select distinct comp.idCompDoc "); // distinct ids
	    queryStr.append("from AroCompDoc comp ");
	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc()) || Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("join comp.aroStrutDoc strutdoc ");
		queryStr.append("join strutdoc.aroDoc doc ");
		queryStr.append("join doc.aroUnitaDoc ud ");
	    }
	    if (Objects.nonNull(filter.getIdDoc())) {
		queryStr.append("join comp.aroStrutDoc strutdoc ");
		queryStr.append("join strutdoc.aroDoc doc ");
	    }

	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("ud.idUnitaDoc = :idUnitaDoc").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("ud.idStrut = :idStrut").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdDoc())) {
		queryStr.append("doc.idDoc = :idDoc").append(QUERY_AND);
	    }

	    if (Objects.nonNull(filter.getIdCompDoc())) {
		queryStr.append("comp.idCompDoc = :idCompDoc").append(QUERY_AND);
	    }
	    // fixed (exclude comp already migrated)
	    queryStr.append(" ( ");
	    queryStr.append(
		    "not exists (select 1 from AroCompObjectStorage cpos where cpos.idCompDoc = comp.idCompDoc)")
		    .append(QUERY_AND);
	    queryStr.append(
		    "exists (select 1 from AroContenutoComp cont where cont.aroCompDoc.idCompDoc = comp.idCompDoc)");
	    queryStr.append("  ) ");
	    // queryStr.append(" order by ud.dtCreazione asc ");

	    //
	    TypedQuery<Long> query = entityManagerSacer.createQuery(queryStr.toString(),
		    Long.class);
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
	    if (Objects.nonNull(filter.getIdDoc())) {
		query.setParameter("idDoc", filter.getIdDoc());
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }
	    if (Objects.nonNull(filter.getIdCompDoc())) {
		query.setParameter("idCompDoc", filter.getIdCompDoc());
	    }

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca dei componenti").build();
	}
    }

    @Override
    public void saveObjectStorageLinkComp(String tenant, String bucket, String key, Long idCompDoc,
	    Long idBackend) {
	AroCompObjectStorage osLink = new AroCompObjectStorage();
	osLink.setIdCompDoc(idCompDoc);

	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

    @Override
    public AroCompDoc findCompDocById(Long idCompDoc) throws AppMigrateOsS3Exception {
	try {
	    return entityManagerSacer.find(AroCompDoc.class, idCompDoc);
	} catch (IllegalArgumentException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e)
		    .message("Componente con id {0,number,#} non trovato", idCompDoc).build();
	}
    }

    @Override
    public void deleteBlContenutoComp(Long idContenutoComp) throws AppMigrateOsDeleteSrcException {

	try {
	    AroContenutoComp contenutoComp = entityManagerSacer.find(AroContenutoComp.class,
		    idContenutoComp);
	    entityManagerSacer.remove(contenutoComp);

	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e)
		    .message("Errore interno nella fase di cancellazione del blob del componente")
		    .build();
	}
    }

    @Override
    @Deprecated(forRemoval = true)
    public Long getIdUnitaDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select ud.idUnitaDoc from AroCompDoc comp ");
	    queryStr.append("join comp.aroStrutDoc strutdoc ");
	    queryStr.append("join strutdoc.aroDoc doc ");
	    queryStr.append("join doc.aroUnitaDoc ud ");
	    queryStr.append("where ");
	    queryStr.append("comp.idCompDoc = :idCompDoc ");

	    //
	    TypedQuery<Long> query = entityManagerSacer.createQuery(queryStr.toString(),
		    Long.class);

	    query.setParameter("idCompDoc", idCompDoc);

	    return query.getSingleResult();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca della unità documentaria, idCompDoc = {0,number,#}",
			    idCompDoc)
		    .build();

	}

    }

    @Override
    @Deprecated(forRemoval = true)
    public Long getIdDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception {
	try {

	    Long idDoc = entityManagerSacer.find(AroCompDoc.class, idCompDoc).getAroStrutDoc()
		    .getAroDoc().getIdDoc();

	    return idDoc;
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca del DOCUMENTO, idCompDoc = {0,number,#}",
			    idCompDoc)
		    .build();

	}
    }

    @Override
    public VrsSessioneVers findVrsSessioneVersByIdStrutAndIdCompDoc(Long idStrut, Long idCompDoc)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select svers from AroCompDoc comp ");
	    queryStr.append("join comp.aroStrutDoc strutdoc ");
	    queryStr.append("join strutdoc.aroDoc doc ");
	    queryStr.append("join doc.aroUnitaDoc ud ");
	    queryStr.append("join ud.vrsSessioneVers svers ");

	    queryStr.append("where ");
	    queryStr.append("svers.tiStatoSessioneVers = :tiStatoSessioneVers ").append(QUERY_AND);
	    queryStr.append("svers.tiSessioneVers = :tiSessioneVers ").append(QUERY_AND);
	    queryStr.append("svers.orgStrut = :idStrut").append(QUERY_AND);
	    queryStr.append("comp.idCompDoc = :idCompDoc");

	    TypedQuery<VrsSessioneVers> query = entityManagerSacer.createQuery(queryStr.toString(),
		    VrsSessioneVers.class);

	    query.setParameter("idCompDoc", idCompDoc);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("tiStatoSessioneVers", CHIUSA_OK.toString());
	    query.setParameter("tiSessioneVers", VERSAMENTO.toString());

	    return query.getSingleResult();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca della sessione versamento, idUnitaDoc = {0,number,#}, idStrut = {1,number,#}",
			    idCompDoc, idStrut)
		    .build();

	}
    }

    @Override
    public Stream<Long> findIdsCompDocOnView(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select compdamig.idCompDoc "); // distinct ids
	    queryStr.append("from AroCompDocDaMigrareOs compdamig ");
	    queryStr.append("where ");
	    queryStr.append("compdamig.idStrut = :idStrut").append(QUERY_AND);
	    // check content comp existence
	    queryStr.append(
		    "exists (select 1 from AroContenutoComp cont where cont.idContenComp = compdamig.idContenComp)");

	    TypedQuery<Long> query = entityManagerSacer.createQuery(queryStr.toString(),
		    Long.class);
	    // optional row limits
	    if (!Objects.isNull(filter.getRowlimit())) {
		query.setMaxResults(filter.getRowlimit().intValue()); // max result
	    }
	    // TODO: check hint for optimizations
	    query.setHint(HibernateHints.HINT_READ_ONLY, true);
	    // query.setHint(HibernateHints.HINT_CACHEABLE, true);
	    // query.setHint(HibernateHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

	    query.setParameter("idStrut", filter.getIdStrut());

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca dei componenti").build();
	}
    }
}
