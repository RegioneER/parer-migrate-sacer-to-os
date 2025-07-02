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

package it.eng.parer.migrate.sacer.os.beans.aip.dao;

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TiStatoSesioneVers.CHIUSA_OK;
import static it.eng.parer.migrate.sacer.os.base.utils.Costants.TipoSessione.VERSAMENTO;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.aip.ISacerAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroFileVerIndiceAipUd;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVerIndiceAipUdObjectStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerAipDao implements ISacerAipDao {
    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";

    @Override
    public Stream<Long> findIdsVerAip(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select arover.idVerIndiceAip "); // ids
	    queryStr.append("from AroFileVerIndiceAipUd filever ");
	    queryStr.append("join filever.aroVerIndiceAipUd arover ");
	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("join arover.aroIndiceAipUd aip ");
		queryStr.append("join aip.aroUnitaDoc ud ");
	    }

	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("ud.idUnitaDoc = :idUnitaDoc").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("filever.idStrut = :idStrut").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdVerIndiceAip())) {
		queryStr.append("arover.idVerIndiceAip = :idVerIndiceAip").append(QUERY_AND);
	    }

	    // fixed (exclude verindiceaip already migrated)
	    queryStr.append(
		    "not exists (select 1 from AroVerIndiceAipUdObjectStorage aipos where aipos.idVerIndiceAipUd = arover.idVerIndiceAip)");

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
	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }
	    if (Objects.nonNull(filter.getIdVerIndiceAip())) {
		query.setParameter("idVerIndiceAip", filter.getIdVerIndiceAip());
	    }

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca degli indici aip").build();
	}
    }

    @Override
    public void saveObjectStorageLinkAip(String tenant, String bucket, String key, Long idSubStrut,
	    BigDecimal aaKeyUnitaDoc, Long idVerIndiceAip, Long idBackend) {
	AroVerIndiceAipUdObjectStorage osLink = new AroVerIndiceAipUdObjectStorage();

	osLink.setIdVerIndiceAipUd(idVerIndiceAip);
	osLink.setIdSubStrut(idSubStrut);
	osLink.setAaKeyUnitaDoc(aaKeyUnitaDoc);
	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);

	osLink.setIdDecBackend(idBackend);
	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

    @Override
    public Object[] findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(Long idVerIndiceAip)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append(
		    "select verindice, filever, unitadoc, sessionevrs from AroFileVerIndiceAipUd filever ");
	    queryStr.append("join filever.aroVerIndiceAipUd verindice ");
	    queryStr.append("join verindice.aroIndiceAipUd indice ");
	    queryStr.append("join indice.aroUnitaDoc unitadoc ");
	    queryStr.append("join unitadoc.vrsSessioneVers sessionevrs ");
	    queryStr.append("where ");
	    queryStr.append("verindice.idVerIndiceAip  = :idVerIndiceAip ").append(QUERY_AND);
	    queryStr.append("sessionevrs.tiStatoSessioneVers  = :tiStatoSessioneVers ")
		    .append(QUERY_AND);
	    queryStr.append("sessionevrs.tiSessioneVers  = :tiSessioneVersUD ");

	    //
	    Query query = entityManagerSacer.createQuery(queryStr.toString());
	    //
	    query.setParameter("idVerIndiceAip", idVerIndiceAip);
	    query.setParameter("tiStatoSessioneVers", CHIUSA_OK.toString());
	    query.setParameter("tiSessioneVersUD", VERSAMENTO.toString());

	    return (Object[]) query.getSingleResult();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore interno nella fase ricerca indice AIP, id = {0,number,#}",
			    idVerIndiceAip)
		    .build();

	}
    }

    @Override
    public void deleteBlFileVerIndiceAip(Long fileVerIndiceAipId)
	    throws AppMigrateOsDeleteSrcException {

	try {
	    AroFileVerIndiceAipUd fileVerIndiceAip = entityManagerSacer
		    .find(AroFileVerIndiceAipUd.class, fileVerIndiceAipId);

	    entityManagerSacer.remove(fileVerIndiceAip);
	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e)
		    .message("Errore interno nella fase aggiornamento clob dell'indice aip ud")
		    .build();
	}
    }
}
