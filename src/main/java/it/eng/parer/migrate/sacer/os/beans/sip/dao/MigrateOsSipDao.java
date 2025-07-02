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
