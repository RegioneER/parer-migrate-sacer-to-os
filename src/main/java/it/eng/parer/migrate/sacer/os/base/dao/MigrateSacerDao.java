package it.eng.parer.migrate.sacer.os.base.dao;

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.DecBackend;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class MigrateSacerDao implements IMigrateSacerDao {

    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    @Override
    public DecBackend findDecBakendByName(String backedName) {
	TypedQuery<DecBackend> query = entityManagerSacer.createQuery(
		"Select d from DecBackend d where d.nmBackend = :nomeBackend", DecBackend.class);
	query.setParameter("nomeBackend", backedName);
	return query.getSingleResult();
    }

    @Override
    public Object[] findNmEnteAndNmStrutByIdStrut(Long idStrut) throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select ente.nmEnte, strut.nmStrut from OrgStrut strut ");
	    queryStr.append("join strut.orgEnte ente ");
	    queryStr.append("where ");
	    queryStr.append("strut.idStrut = :idStrut ");

	    //
	    Query query = entityManagerSacer.createQuery(queryStr.toString());
	    //
	    query.setParameter("idStrut", idStrut);

	    return (Object[]) query.getSingleResult();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca nmStrut e nmEnte, idStrut = {0,number,#}",
			    idStrut)
		    .build();

	}
    }
}
