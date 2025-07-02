package it.eng.parer.migrate.sacer.os.beans.sip.dao;

import java.util.Arrays;

import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class MigrateOsSipDao {

    @Inject
    protected EntityManager entityManager;

    private static final String QUERY_AND = " and ";

    public boolean findVrsSessioneVersOnAlreadyDone(VrsSessioneVers vrsSessioneVers) {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select count(os) ");
	    queryStr.append("from ObjectStorage os ");
	    queryStr.append("where ");
	    queryStr.append("os.idSessioneVers = :idSessioneVers ").append(QUERY_AND);
	    queryStr.append("os.state in (:state) ");

	    //
	    TypedQuery<Long> query = entityManager.createQuery(queryStr.toString(), Long.class);

	    query.setParameter("idSessioneVers", vrsSessioneVers.getIdSessioneVers());
	    query.setParameter("state", Arrays.asList(ObjectStorageCnts.State.MIGRATED));

	    Long result = query.getSingleResult();
	    return result.intValue() > 0;
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore interno nella fase ricerca delle sessioni di versamento")
		    .build();
	}

    }

}
