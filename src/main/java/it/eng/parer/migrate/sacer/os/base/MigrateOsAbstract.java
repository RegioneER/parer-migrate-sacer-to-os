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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

public abstract class MigrateOsAbstract {

    private static final Logger log = LoggerFactory.getLogger(MigrateOsAbstract.class);

    @ConfigProperty(name = "parer.migrate.sacer.os.persist-completed.enabled")
    boolean persistCompletedEnabled;

    @Inject
    IMigrateOsDao osDao;

    @Inject
    IMigrateSacerDao sacerDao;

    @Inject
    IMigrateOsService osService;

    @Inject
    IMigrateSacerService sacerService;

    /**
     * Da implementare : ricerca identificativi oggetti da migrare
     *
     * @param filter filtro da applicare per ricerca
     *
     * @return Stream di pk
     */
    protected abstract Stream<Long> findObjIdsByFilter(FilterDto filter);

    /**
     * Processa la richiesta di migrazione recuperando su {@link Stream} la lista degli
     * identificativi ed effettua migrazione via S3 per singolo oggetto recuperato dallo stream.
     *
     * @param idRequest      id request migrazione
     * @param filterDto      lista ids da migrare
     * @param deleteSrc      cancellazione del dato se migrazione terminata correttamente
     * @param idSacerBackend id backend S3
     */
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void processMigrationRequest(Long idRequest) {
	// get request
	RequestDto request = osService.getRequestById(idRequest);
	// get backend migration
	Long idSacerBackend = sacerService.getIdDeckBackeEnd();
	// get the ids list
	Stream<Long> objIds = findObjIdsByFilter(request.getFilter());

	// counters
	AtomicInteger countNrFound = new AtomicInteger();
	AtomicInteger countNrDone = new AtomicInteger();
	//
	// forEach
	objIds.forEach(objId -> {
	    LocalDateTime start = LocalDateTime.now();
	    log.atInfo().log("Inizio migrazione {} id {}", getObjType(), objId);
	    // update request (count nr of session migrated correctly + founded)
	    osService.updateOsRequest(idRequest, RequestCnts.State.IN_PROGRESS, Optional.empty(),
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.empty(), Optional.of(Long.valueOf(countNrFound.incrementAndGet())),
		    Optional.empty(), Optional.empty(), Optional.empty());
	    try {
		// migrate execution
		IObjectStorageResource osresource = executeMigrateViaS3(idSacerBackend, objId,
			request.getDeleteSrc());
		// update session vers registerd
		if (persistCompletedEnabled) {
		    osService
			    .createOsObjectStorageOfObject(idRequest, objId,
				    ObjectStorageCnts.State.MIGRATED, getObjType(),
				    Optional.of(osresource.getS3Bucket()),
				    Optional.of(osresource.getS3Key()),
				    Optional.of(osresource.getObjBase64()),
				    Optional.ofNullable(osresource.getSHA256()),
				    Optional.of(ObjectStorageCnts.IntegrityType
					    .valueOf(osresource.getS3Checksum())),
				    Optional.empty());
		}

		// update request (count nr of session migrated correctly + founded)
		osService.updateOsRequest(idRequest, RequestCnts.State.IN_PROGRESS,
			Optional.empty(),
			Optional.of(LocalDateTime.now().atZone(ZoneId.systemDefault())
				.toLocalDateTime()),
			Optional.empty(), Optional.empty(),
			Optional.of(Long.valueOf(countNrDone.incrementAndGet())), Optional.empty(),
			Optional.empty());

		LocalDateTime end = LocalDateTime.now();
		log.atInfo().log("Fine migrazione {} id {} in {} ms", getObjType(), objId,
			Duration.between(start, end).toMillis());
	    } catch (AppMigrateOsS3Exception e) {
		log.atError().log("Errore migrazione {} id {}", getObjType(), objId, e);
		// update register session vers with error (go next element)
		osService.createOsObjectStorageOfObject(idRequest, objId,
			ObjectStorageCnts.State.MIGRATION_ERROR, getObjType(),
			Objects.isNull(e.getOsresouce()) ? Optional.empty()
				: Optional.of(e.getOsresouce().getS3Bucket()),
			Objects.isNull(e.getOsresouce()) ? Optional.empty()
				: Optional.of(e.getOsresouce().getS3Key()),
			Objects.isNull(e.getOsresouce()) ? Optional.empty()
				: Optional.of(e.getOsresouce().getObjBase64()),
			Objects.isNull(e.getOsresouce()) ? Optional.empty()
				: Optional.ofNullable(e.getOsresouce().getSHA256()),
			Objects.isNull(e.getOsresouce()) ? Optional.empty()
				: Optional.of(ObjectStorageCnts.IntegrityType
					.valueOf(e.getOsresouce().getS3Checksum())),
			Optional.of(ExceptionUtils.getStackTrace(e)));
	    }
	});

	// 5. update request
	osService.updateOsRequest(idRequest, RequestCnts.State.FINISHED, Optional.empty(),
		Optional.of(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		Optional.of(LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Effettua la procedura di migrazione del singolo oggetto via S3
     *
     * @param backend
     *
     * @param objId     identificativo oggetto da migrare
     * @param deleteSrc cancellazione del dato su database SACER, true = effettua cancellazione /
     *                  false = altrimenti
     *
     * @return coordinate su OS
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    protected abstract IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception;

    /**
     * Regitrazione richiesta di migrazione
     *
     * @param osRequests richiesta/e da registrare e verificare
     * @param objType    tipologia oggetto da migrare (vedi {@link Type})
     *
     * @return richiesta/e aggiornate
     */
    public List<RequestDto> registerRequestByType(List<MigrateRequest> osRequests,
	    RequestCnts.Type objType) {
	// call base serviceMock
	return osService.registerOsRequestByType(osRequests, objType);
    }

    /**
     * Da implemtare : definizione del tipo di oggetto da gestire in migrazione (vedi {@link Type})
     *
     * @return restituisce il tipo di oggetto {@link Type}
     */
    protected abstract ObjectStorageCnts.ObjectType getObjType();

}
