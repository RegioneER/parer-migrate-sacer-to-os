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

package it.eng.parer.migrate.sacer.os.beans.datispecvers.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.jpa.HibernateHints;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.ISacerDatiSpecVersDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniComp;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniDatiSpec;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniDatiSpecObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroVersIniDatiSpecObjectStorageCnts.TiEntitaSacerAroVersIniDatiSpecOs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class SacerDatiSpecVersDao implements ISacerDatiSpecVersDao {
    @Inject
    @PersistenceUnit(unitName = "sacer")
    EntityManager entityManager;

    private static final String QUERY_AND = " and ";

    @Override
    public Stream<Long> findIdsDatiSpecVers(FilterDto filter) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select distinct datispecvers.aroVersIniUnitaDoc.idVersIniUnitaDoc ");
	    queryStr.append("from AroVersIniDatiSpec datispecvers ");
	    queryStr.append("where ");

	    // dinamyc params
	    if (Objects.nonNull(filter.getIdUnitadoc())) {
		queryStr.append(
			"datispecvers.aroVersIniUnitaDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc")
			.append(QUERY_AND);
	    }
	    if (Objects.nonNull(filter.getIdStrut())) {
		queryStr.append("datispecvers.orgStrut.idStrut = :idStrut").append(QUERY_AND);
	    }
	    // fixed (exclude sip already migrated)
	    queryStr.append(
		    " not exists (select 1 from AroVersIniDatiSpecObjectStorage udos where udos.aroVersIniUnitaDoc.idVersIniUnitaDoc = datispecvers.aroVersIniUnitaDoc.idVersIniUnitaDoc)");

	    //
	    TypedQuery<Long> query = entityManager.createQuery(queryStr.toString(), Long.class);
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
			    "Errore interno nella fase ricerca dei dati specifici di versamento ud.")
		    .build();
	}
    }

    @Override
    public void saveObjectStorageLinkDatiSpecVers(String tenant, String bucket, String key,
	    Long idStrut, DatiSpecLinkOsKeyMap datiSpec, Long idBackend) {
	AroVersIniDatiSpecObjectStorage osLink = new AroVersIniDatiSpecObjectStorage();

	switch (datiSpec.getTipiEntitaSacer()) {
	case "UNI_DOC":
	    AroVersIniUnitaDoc aroVersIniUnitaDoc = entityManager.find(AroVersIniUnitaDoc.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroVersIniUnitaDoc(aroVersIniUnitaDoc);
	    break;
	case "DOC":
	    AroVersIniDoc aroVersIniDoc = entityManager.find(AroVersIniDoc.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroVersIniUnitaDoc(aroVersIniDoc.getAroVersIniUnitaDoc());
	    osLink.setAroVersIniDoc(aroVersIniDoc);
	    break;
	case "COMP":
	    AroVersIniComp aroVersIniComp = entityManager.find(AroVersIniComp.class,
		    datiSpec.getIdEntitySacer());
	    osLink.setAroVersIniUnitaDoc(aroVersIniComp.getAroVersIniDoc().getAroVersIniUnitaDoc());
	    osLink.setAroVersIniDoc(aroVersIniComp.getAroVersIniDoc());
	    osLink.setAroVersIniComp(aroVersIniComp);
	    break;
	default:
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).message(
		    "Errore nella fase di salvataggio link dati specifi iniziali unità documentaria, tipologia entità non riconosciuta: {0}",
		    datiSpec.getTipiEntitaSacer()).build();
	}

	osLink.setTiEntitaSacer(
		TiEntitaSacerAroVersIniDatiSpecOs.valueOf(datiSpec.getTipiEntitaSacer()));
	osLink.setCdKeyFile(key);
	osLink.setNmBucket(bucket);
	osLink.setNmTenant(tenant);
	osLink.setIdDecBackend(idBackend);
	osLink.setIdStrut(BigDecimal.valueOf(idStrut));

	entityManager.persist(osLink);
	entityManager.flush();
    }

    @Override
    public void deleteBlDatiSpecVers(List<Long> versIniDatiSpecIds)
	    throws AppMigrateOsDeleteSrcException {
	try {
	    versIniDatiSpecIds.forEach(versIniDatiSpecId -> {
		AroVersIniDatiSpec aroVersIniDatiSpec = entityManager.find(AroVersIniDatiSpec.class,
			versIniDatiSpecId);
		aroVersIniDatiSpec.setBlXmlDatiSpec(null); // update to null
		entityManager.persist(aroVersIniDatiSpec);
	    });
	    entityManager.flush();
	} catch (Exception e) {
	    throw AppMigrateOsDeleteSrcException.builder().cause(e).message(
		    "Errore interno nella fase aggiornamento clob XML dei dati specifici di versamento")
		    .build();
	}
    }

    @Override
    public List<AroVersIniDatiSpec> findDatiSpecVersByIdVersIniUnitaDoc(Long idVersIniUnitaDoc)
	    throws AppMigrateOsS3Exception {
	try {
	    AroVersIniUnitaDoc aroVersIniUnitaDoc = entityManager.find(AroVersIniUnitaDoc.class,
		    idVersIniUnitaDoc);

	    return aroVersIniUnitaDoc.getAroVersIniDatiSpecs();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message(
			    "Errore interno nella fase ricerca dei dati specifici di versamento ud.")
		    .build();
	}
    }

    /**
     * Recupera un oggetto AroVersIniUnitaDoc dato il suo ID.
     *
     * @param idVersIniUnitaDoc ID dell'unità documento iniziale.
     *
     * @return Oggetto AroVersIniUnitaDoc corrispondente all'ID fornito.
     *
     * @throws AppGenericRuntimeException In caso di errore durante il recupero.
     */
    public AroVersIniUnitaDoc findAroVersIniUnitaDocById(Long idVersIniUnitaDoc) {
	try {
	    return entityManager.find(AroVersIniUnitaDoc.class, idVersIniUnitaDoc);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore durante il recupero di AroVersIniUnitaDoc con ID {0,number,#}",
			    idVersIniUnitaDoc)
		    .build();
	}
    }

}
