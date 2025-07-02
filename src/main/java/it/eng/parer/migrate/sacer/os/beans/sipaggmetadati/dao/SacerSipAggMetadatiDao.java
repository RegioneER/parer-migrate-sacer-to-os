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

package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.dao;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.ISacerSipAggMetadatiDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUpdUdObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUpdUnitaDoc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerSipAggMetadatiDao implements ISacerSipAggMetadatiDao {
    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManagerSacer;

    private static final String QUERY_AND = " and ";

    @Override
    public AroUpdUnitaDoc findAroUpdUnitaDocById(Long idUpdUnitaDoc)
	    throws AppMigrateOsS3Exception {
	try {
	    return entityManagerSacer.find(AroUpdUnitaDoc.class, idUpdUnitaDoc);
	} catch (IllegalArgumentException e) {
	    throw AppMigrateOsS3Exception.builder().cause(e)
		    .message("Aggiornamento metadati ud con id {0,number,#} non trovato",
			    idUpdUnitaDoc)
		    .build();
	}
    }

    @Override
    public Stream<Long> findIdsUpdUnitaDoc(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select upd.idUpdUnitaDoc ");
	    queryStr.append("from AroUpdUnitaDoc upd ");
	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append("upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc").append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("upd.idStrut = :idStrut").append(QUERY_AND);
	    }
	    // fixed (exclude sip already migrated)
	    queryStr.append(
		    " not exists (select 1 from AroXmlUpdUdObjectStorage udos where udos.idUpdUnitaDoc = upd.idUpdUnitaDoc)");

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
	    if (Objects.nonNull(filter.getIdStrut())) {
		query.setParameter("idStrut", filter.getIdStrut());
	    }

	    return query.getResultStream();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase ricerca dei sip di aggiornamento metadati ud.")
		    .build();
	}
    }

    public List<AroXmlUpdUnitaDoc> findAllXmlUpdUnitaDocByIdUpdUnitaDoc(Long idUpdUnitaDoc)
	    throws AppMigrateOsS3Exception {
	StringBuilder queryStr = new StringBuilder();
	try {

	    queryStr.append("select xml from AroXmlUpdUnitaDoc xml ");
	    queryStr.append("join xml.aroUpdUnitaDoc aroupd ");
	    queryStr.append("where ");
	    queryStr.append("aroupd.idUpdUnitaDoc = :idUpdUnitaDoc ");

	    //
	    TypedQuery<AroXmlUpdUnitaDoc> query = entityManagerSacer
		    .createQuery(queryStr.toString(), AroXmlUpdUnitaDoc.class);

	    query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);

	    return query.getResultList();
	} catch (Exception e) {
	    throw AppMigrateOsS3Exception.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca dei sip di aggiornamento metadati ud, id = {0,number,#}",
			    idUpdUnitaDoc)
		    .build();

	}
    }

    @Override
    public void deleteBlXmlOnAroXmlUpdUnitaDoc(List<Long> xmlUpdUnitaDocIds)
	    throws AppMigrateOsDeleteSrcException {
	try {
	    xmlUpdUnitaDocIds.forEach(xmlUpdUnitaDocId -> {
		AroXmlUpdUnitaDoc xmlUpdUnitaDoc = entityManagerSacer.find(AroXmlUpdUnitaDoc.class,
			xmlUpdUnitaDocId);
		xmlUpdUnitaDoc.setBlXml(null); // update to null
		entityManagerSacer.persist(xmlUpdUnitaDoc);
	    });
	    entityManagerSacer.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento clob XML dei dati di aggiornamento metadati")
		    .build();
	}
    }

    @Override
    public void saveObjectStorageLinkSipUdAggMd(String tenant, String bucket, String key,
	    long idUpdUnitaDoc, long idStrut, Long idSacerBackend) {
	AroXmlUpdUdObjectStorage osLink = new AroXmlUpdUdObjectStorage();
	osLink.setIdUpdUnitaDoc(idUpdUnitaDoc);

	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);
	osLink.setIdStrut(idStrut);

	osLink.setIdDecBackend(idSacerBackend);

	entityManagerSacer.persist(osLink);
	entityManagerSacer.flush();
    }

}
