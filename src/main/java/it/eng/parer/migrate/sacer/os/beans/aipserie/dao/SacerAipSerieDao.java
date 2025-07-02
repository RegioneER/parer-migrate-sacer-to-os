package it.eng.parer.migrate.sacer.os.beans.aipserie.dao;

import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.aipserie.ISacerAipSerieDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.SerFileVerSerie;
import it.eng.parer.migrate.sacer.os.jpa.sacer.SerVerSerieObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.SerFileVerSerieCnts.TiFileVerSerie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerAipSerieDao implements ISacerAipSerieDao {

    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";

    @Override
    public Stream<Long> findIdsVerSerie(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select filever.idFileVerSerie "); // ids
	    queryStr.append("from SerFileVerSerie filever ");
	    queryStr.append("join filever.serVerSerie server ");

	    queryStr.append("where ");

	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("filever.idStrut = :idStrut").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdVerSerie())) {
		queryStr.append("server.idVerSerie = :idVerSerie").append(QUERY_AND);
	    }

	    // fixed (exclude verindiceaip already migrated)
	    queryStr.append(
		    "not exists (select 1 from SerVerSerieObjectStorage serveros where serveros.idVerSerie = server.idVerSerie)");

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

	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }
	    if (Objects.nonNull(filter.getIdVerSerie())) {
		query.setParameter("idVerSerie", filter.getIdVerSerie());
	    }

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca degli indici aip di serie").build();
	}
    }

    @Override
    public void saveObjectStorageLinkAipSerie(String tenant, String bucket, String key,
	    Long idStrut, Long idVerIndiceAip, TiFileVerSerie tiFileVerSerie, Long idBackend) {
	SerVerSerieObjectStorage osLink = new SerVerSerieObjectStorage();

	osLink.setIdVerSerie(idVerIndiceAip);
	osLink.setTiFileVerSerie(tiFileVerSerie.name());
	osLink.setIdStrut(idStrut);
	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

    @Override
    public Object[] findSerSerieAndIndiceAndFileAipByIdFileVerSerie(Long idFileVerSerie)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select serie, verindice, filever from SerFileVerSerie filever ");
	    queryStr.append("join filever.serVerSerie verindice ");
	    queryStr.append("join verindice.serSerie serie ");
	    queryStr.append("where ");
	    queryStr.append("filever.idFileVerSerie  = :idFileVerSerie ");

	    //
	    Query query = entityManagerSacer.createQuery(queryStr.toString());
	    //
	    query.setParameter("idFileVerSerie", idFileVerSerie);

	    return (Object[]) query.getSingleResult();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca indice AIP serie, idFileVerSerie = {0,number,#}",
			    idFileVerSerie)
		    .build();

	}
    }

    @Override
    public void deleteBlFileVerSerie(Long idFileVerSerie) throws AppMigrateOsDeleteSrcException {
	try {
	    SerFileVerSerie fileVerIndiceAip = entityManagerSacer.find(SerFileVerSerie.class,
		    idFileVerSerie);

	    fileVerIndiceAip.setBlFile(null);
	    entityManagerSacer.persist(fileVerIndiceAip);
	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e)
		    .message("Errore interno nella fase aggiornamento clob dell'indice aip ud")
		    .build();
	}
    }

}
