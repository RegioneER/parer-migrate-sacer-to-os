/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.migrate.sacer.os.beans.job.dao;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class MigrateOsJobDao implements IMigrateOsJobDao {

    @Inject
    EntityManager entityManager;

    private static final String QUERY_AND = " and ";

    @Override
    public boolean exceedNrRequestRunningPerHost(Integer hostMaxReq, String hostname) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select count(*) ");
	    queryStr.append("from Requests req ");
	    queryStr.append("where ");
	    queryStr.append("req.state not in (:state)").append(QUERY_AND); // fixed
	    queryStr.append("req.hostname = :hostname").append(QUERY_AND); // fixed

	    // clean
	    String queryClean = StringUtils.removeEnd(queryStr.toString(), QUERY_AND);

	    //
	    TypedQuery<Long> query = entityManager.createQuery(queryClean, Long.class);
	    query.setParameter("state", Arrays.asList(RequestCnts.State.REGISTERED,
		    RequestCnts.State.ERROR, RequestCnts.State.FINISHED));
	    query.setParameter("hostname", hostname);

	    return query.getSingleResult() >= hostMaxReq;
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase di verifica numero massimo {} richieste gestite da singolo host {}",
			    hostMaxReq, hostname)
		    .build();
	}
    }

    @Override
    public boolean notExitsRequestRegisteredByType(RequestCnts.Type type) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("from Requests ");
	    queryStr.append("where ");
	    queryStr.append("state = :state").append(QUERY_AND); // fixed
	    queryStr.append("migrationType = :migrationType").append(QUERY_AND); // fixed
	    // clean
	    String queryClean = StringUtils.removeEnd(queryStr.toString(), QUERY_AND);

	    //
	    TypedQuery<Requests> query = entityManager.createQuery(queryClean, Requests.class);

	    query.setParameter("state", RequestCnts.State.REGISTERED);
	    query.setParameter("migrationType", type);

	    return query.getResultList().isEmpty();

	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca delle request").build();
	}
    }

}
