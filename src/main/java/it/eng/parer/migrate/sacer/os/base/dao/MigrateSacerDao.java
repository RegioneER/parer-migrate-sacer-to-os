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
