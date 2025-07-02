package it.eng.parer.migrate.sacer.os.beans.sip.dao;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TI_XML_SESSIONE_VERS;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TiStatoSesioneVers.CHIUSA_OK;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.AGGIUNGI_DOCUMENTO;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.VERSAMENTO;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.sip.ISacerSipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlDocObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUnitaDocObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsXmlDatiSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerSipDao implements ISacerSipDao {

    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";
    private static final String QUERY_OR = " or ";

    @Override
    public Stream<Long> findIdsSessVersUdDocChiusaOk(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select distinct svers.idSessioneVers "); // distinct ids
	    queryStr.append("from VrsSessioneVers svers ");
	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("svers.aroUnitaDoc.idUnitaDoc = :idUnitaDoc").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdDoc())) {
		queryStr.append("svers.aroDoc.idDoc = :idDoc").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdSessioneVers())) {
		queryStr.append("svers.idSessioneVers = :idSessioneVers").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("svers.orgStrut = :idStrut").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getDtApertura())) {
		queryStr.append(
			"svers.dtApertura >= :dtAperturaFrom and svers.dtApertura < :dtAperturaTo")
			.append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getDtAperturaYY())) {
		queryStr.append("svers.dtApertura between :dtAperturaYYFrom and :dtAperturaYYTo")
			.append(QUERY_AND);
	    }
	    // fixed (exclude vrs session already migrated)
	    queryStr.append("( ");
	    queryStr.append(" ( ").append("svers.tiSessioneVers  = :tiSessioneVersUD")
		    .append(QUERY_AND);
	    queryStr.append(
		    "not exists (select 1 from AroXmlUnitaDocObjectStorage udos where udos.idUnitaDoc = svers.aroUnitaDoc.idUnitaDoc)")
		    .append(" ) ");
	    queryStr.append(QUERY_OR); // OR
	    queryStr.append(" ( ").append("svers.tiSessioneVers  = :tiSessioneVersDOC")
		    .append(QUERY_AND);
	    queryStr.append(
		    " not exists (select 1 from AroXmlDocObjectStorage udos where udos.idDoc = svers.aroDoc.idDoc)")
		    .append(" ) ");
	    queryStr.append("  ) ").append(QUERY_AND);

	    // fixed
	    queryStr.append(" svers.tiStatoSessioneVers = :tiStatoSessioneVers ");
	    // queryStr.append(" order by svers.dtApertura asc ");

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
	    if (Objects.nonNull(filter.getIdSessioneVers())) {
		query.setParameter("idSessioneVers", filter.getIdSessioneVers());
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }
	    if (Objects.nonNull(filter.getDtApertura())) {
		query.setParameter("dtAperturaFrom", filter.getDtApertura());
		query.setParameter("dtAperturaTo", filter.getDtApertura().plusDays(1));
	    }
	    if (Objects.nonNull(filter.getDtAperturaYY())) {
		query.setParameter("dtAperturaYYFrom",
			LocalDate.of(filter.getDtAperturaYY(), Month.JANUARY, 1));
		query.setParameter("dtAperturaYYTo",
			LocalDate.of(filter.getDtAperturaYY(), Month.DECEMBER, 31));
	    }
	    // fixed
	    query.setParameter("tiStatoSessioneVers", CHIUSA_OK.toString());
	    query.setParameter("tiSessioneVersUD", VERSAMENTO.toString());
	    query.setParameter("tiSessioneVersDOC", AGGIUNGI_DOCUMENTO.toString());

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca delle Sessioni di versamento")
		    .build();
	}
    }

    @Override
    public void saveObjectStorageLinkSipUd(String tenant, String bucket, String key,
	    Long idUnitaDoc, Long idBackend) {
	AroXmlUnitaDocObjectStorage osLink = new AroXmlUnitaDocObjectStorage();
	osLink.setIdUnitaDoc(idUnitaDoc);

	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

    @Override
    public void saveObjectStorageLinkSipDoc(String tenant, String bucket, String key, Long idDoc,
	    Long idBackend) {
	AroXmlDocObjectStorage osLink = new AroXmlDocObjectStorage();
	osLink.setIdDoc(idDoc);

	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush(); // needed for managing rollback
    }

    @Override
    public List<VrsXmlDatiSessioneVers> findAllXmlDatiSessioneVersByIdSessVers(Long idSessioneVers)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select xml from VrsXmlDatiSessioneVers xml ");
	    queryStr.append("join xml.vrsDatiSessioneVers dati ");
	    queryStr.append("join dati.vrsSessioneVers sess ");
	    queryStr.append("where ");
	    queryStr.append("sess.idSessioneVers  = :idSessioneVers ").append(QUERY_AND);
	    queryStr.append("sess.tiStatoSessioneVers = :tiStatoSessioneVers ").append(QUERY_AND);
	    queryStr.append("dati.tiDatiSessioneVers = :tiDatiSessioneVers ");
	    // order by
	    queryStr.append("order by sess.dtChiusura");

	    //
	    TypedQuery<VrsXmlDatiSessioneVers> query = entityManagerSacer
		    .createQuery(queryStr.toString(), VrsXmlDatiSessioneVers.class);

	    query.setParameter("idSessioneVers", idSessioneVers);
	    query.setParameter("tiStatoSessioneVers", CHIUSA_OK.toString());
	    query.setParameter("tiDatiSessioneVers", TI_XML_SESSIONE_VERS);

	    return query.getResultList();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca XML di sessione versamento, id = {0,number,#}",
			    idSessioneVers)
		    .build();

	}
    }

    @Override
    public VrsSessioneVers findVrsSessioneVersById(Long idSessioneVers)
	    throws AppMigrateOsS3Exception {
	try {
	    return entityManagerSacer.find(VrsSessioneVers.class, idSessioneVers);
	} catch (IllegalArgumentException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e)
		    .message("Sessione di versamento con id {0,number,#} non trovata",
			    idSessioneVers)
		    .build();
	}
    }

    @Override
    public void deleteBlXmlOnVrsXmlDatiSessioneVers(List<Long> xmlDatiSessioneVersIds)
	    throws AppMigrateOsDeleteSrcException {

	try {
	    xmlDatiSessioneVersIds.forEach(idXmlDatiSessioneVers -> {
		VrsXmlDatiSessioneVers xmlDatiSessioneVers = entityManagerSacer
			.find(VrsXmlDatiSessioneVers.class, idXmlDatiSessioneVers);
		xmlDatiSessioneVers.setBlXml(null); // update to null
		entityManagerSacer.persist(xmlDatiSessioneVers);
	    });

	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento clob XML dei dati di sessione versamento")
		    .build();
	}
    }

}
