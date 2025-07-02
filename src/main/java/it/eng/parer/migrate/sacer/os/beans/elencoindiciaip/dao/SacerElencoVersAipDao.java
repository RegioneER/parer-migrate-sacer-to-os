
package it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.ISacerElencoVersAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvElencoVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvFileElencoVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvFileElencoVersObjectStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerElencoVersAipDao implements ISacerElencoVersAipDao {

    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";
    private static final String FIRMA_ELENCO_INDICI_AIP = "FIRMA_ELENCO_INDICI_AIP";
    private static final String MARCA_FIRMA_ELENCO_INDICI_AIP = "MARCA_FIRMA_ELENCO_INDICI_AIP";
    private static final List<String> tiFileElencoVersList = Arrays.asList(FIRMA_ELENCO_INDICI_AIP,
	    MARCA_FIRMA_ELENCO_INDICI_AIP);

    @Override
    public Stream<Long> findIdsElvElencoVers(FilterDto filter) {
	try {
	    // Costruisce la query dinamica
	    String queryStr = buildElvElencoVersAipQuery(filter);

	    // Crea la query
	    TypedQuery<Long> query = entityManagerSacer.createQuery(queryStr, Long.class);

	    // Imposta i parametri della query
	    setElvElencoVersAipQueryParameters(query, filter);

	    // Imposta eventuali limiti di riga
	    if (Objects.nonNull(filter.getRowlimit())) {
		query.setMaxResults(filter.getRowlimit().intValue());
	    }

	    // Ottimizzazioni Hibernate
	    query.setHint(HibernateHints.HINT_READ_ONLY, true);

	    // Restituisce lo stream dei risultati
	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca degli elenchi indici aip di ud.")
		    .build();
	}
    }

    /**
     * Costruisce la query dinamica per trovare gli ID di ElvElencoVers.
     *
     * @param filter Filtro contenente i criteri di ricerca.
     *
     * @return Stringa della query costruita dinamicamente.
     */
    private String buildElvElencoVersAipQuery(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	queryStr.append("select fileelv.idFileElencoVers ");
	queryStr.append("from ElvElencoVers elv join elv.elvFileElencoVers fileelv ");
	// Aggiunge join opzionale
	if (Objects.nonNull(filter.getIdUnitadoc())) {
	    queryStr.append("join elv.aroUnitaDocs ud ");
	}

	// fix
	queryStr.append("where fileelv.tiFileElencoVers IN (:tiFileElencoVers) ").append(QUERY_AND);

	// Aggiunge condizioni dinamiche
	if (Objects.nonNull(filter.getIdUnitadoc())) {
	    queryStr.append("ud.idUnitaDoc = :idUnitaDoc").append(QUERY_AND);
	}
	if (Objects.nonNull(filter.getIdStrut())) {
	    queryStr.append("elv.idStrut = :idStrut").append(QUERY_AND);
	}
	if (Objects.nonNull(filter.getIdElencoVers())) {
	    queryStr.append("elv.idElencoVers = :idElencoVers").append(QUERY_AND);
	}

	// Condizione per escludere file già migrati
	queryStr.append(
		"not exists (select 1 from ElvFileElencoVersObjectStorage elvos where fileelv.idFileElencoVers = elvos.idFileElencoVers)");

	return queryStr.toString();
    }

    /**
     * Imposta i parametri della query per trovare gli ID di ElvElencoVers.
     *
     * @param query  Query a cui impostare i parametri.
     * @param filter Filtro contenente i valori dei parametri.
     */
    private void setElvElencoVersAipQueryParameters(TypedQuery<Long> query, FilterDto filter) {
	// Parametri obbligatori
	query.setParameter("tiFileElencoVers", tiFileElencoVersList);

	// Parametri opzionali
	if (Objects.nonNull(filter.getIdUnitadoc())) {
	    query.setParameter("idUnitaDoc", filter.getIdUnitadoc());
	}
	if (Objects.nonNull(filter.getIdStrut())) {
	    query.setParameter("idStrut", filter.getIdStrut());
	}
	if (Objects.nonNull(filter.getIdElencoVers())) {
	    query.setParameter("idElencoVers", filter.getIdElencoVers());
	}
    }

    @Override
    public void saveObjectStorageLinkElencoIndiciAipUd(String tenant, String bucket, String key,
	    Long idFileElencoVers, Long idBackend) throws AppMigrateOsS3Exception {
	ElvFileElencoVersObjectStorage osLink = new ElvFileElencoVersObjectStorage();
	ElvFileElencoVers elvFile = entityManagerSacer.find(ElvFileElencoVers.class,
		idFileElencoVers);
	osLink.setIdFileElencoVers(idFileElencoVers);

	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);
	osLink.setIdStrut(elvFile.getIdStrut());
	osLink.setIdDecBackend(idBackend);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

    @Override
    public ElvFileElencoVers findFileElencoVersByIdFileElencoVers(Long idFileElencoVers)
	    throws AppMigrateOsS3Exception {
	try {
	    return entityManagerSacer.find(ElvFileElencoVers.class, idFileElencoVers);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase ricerca del file dell'elenco di versamento ud.")
		    .build();
	}
    }

    @Override
    public ElvElencoVers findElencoVersById(Long idElencoVers) throws AppMigrateOsS3Exception {
	try {
	    return entityManagerSacer.find(ElvElencoVers.class, idElencoVers);
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento blob dell'elenco indici aip ud.")
		    .build();
	}
    }

    @Override
    public void deleteBlFileElencoVers(Long idFileElencoVers)
	    throws AppMigrateOsDeleteSrcException {
	try {
	    ElvFileElencoVers fileElv = entityManagerSacer.find(ElvFileElencoVers.class,
		    idFileElencoVers);
	    fileElv.setBlFileElencoVers(null); // update to null
	    entityManagerSacer.persist(fileElv);

	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento blob dell'elenco indici aip ud.")
		    .build();
	}
    }

}
