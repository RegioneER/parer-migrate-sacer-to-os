/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.WorkerExecutor;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

public abstract class MigrateOsAbstract {

    private static final Logger log = LoggerFactory.getLogger(MigrateOsAbstract.class);

    // flag di shutdown in corso
    private volatile boolean shuttingDown = false;
    // contatore batch in esecuzione
    private final AtomicInteger runningBatches = new AtomicInteger(0);

    @ConfigProperty(name = "parer.migrate.sacer.os.persist-completed.enabled")
    boolean persistCompletedEnabled;

    @ConfigProperty(name = "job.thread-pool-max", defaultValue = "1")
    Integer threadPoolMax;

    // tempo massimo di esecuzione del thread in minutes (default 1 ora)
    @ConfigProperty(name = "job.thread-max-execute-time", defaultValue = "60")
    Integer maxExecuteTime;

    // tempo massimo di esecuzione del loop di evento in ns (default 1 ora)
    @ConfigProperty(name = "job.max-eventloop-execute-time", defaultValue = "60")
    Long maxEventLoopExecuteTime;

    @ConfigProperty(name = "job.batch-size", defaultValue = "1")
    Integer batchSize;

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
        // check shutdown in progress
        if (shuttingDown) {
            log.atWarn().log(
                    "Gracefulshutdown in corso: non viene avviata nuova migrazione per request id {}",
                    idRequest);
            return;
        }
        // get request
        RequestDto request = osService.getRequestById(idRequest);
        // get backend migration
        Long idSacerBackend = sacerService.getIdDeckBackeEnd();
        // get the ids list
        Stream<Long> objIds = findObjIdsByFilter(request.getFilter());

        // counters
        AtomicInteger countNrFound = new AtomicInteger();
        AtomicInteger countNrDone = new AtomicInteger();

        // Create dedicated Vert.x instance for this migration request
        VertxOptions options = new VertxOptions()
                .setMaxEventLoopExecuteTime(maxEventLoopExecuteTime)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES); // 1 hour in minutes
        Vertx vertx = Vertx.vertx(options);
        WorkerExecutor executor = vertx.createSharedWorkerExecutor(
                "migration-executor-" + request.getUuid(), threadPoolMax, maxExecuteTime,
                TimeUnit.MINUTES);
        //
        List<Long> buffer = new ArrayList<>(batchSize);
        //
        LocalDateTime start = LocalDateTime.now();
        log.atInfo().log(
                "Inizio lavorazione richiesta UUID {}, nr. of tasks {} and processing via batch size {}",
                request.getUuid(), threadPoolMax, batchSize);

        try {
            // process items in batches
            objIds.forEach(objId -> {
                // check shutdown in progress
                if (shuttingDown) {
                    log.atWarn().log(
                            "Gracefulshutdown in corso: interrotta migrazione per request id {}",
                            idRequest);
                    return;
                }
                // add to buffer
                buffer.add(objId);
                // if buffer is full, process batch
                if (buffer.size() == batchSize) {
                    // process batch
                    processBatch(buffer, executor, idRequest, idSacerBackend, request, countNrFound,
                            countNrDone);
                    // clear buffer
                    buffer.clear();
                }
            });
            // process any remaining items
            if (!buffer.isEmpty() && !shuttingDown) {
                processBatch(buffer, executor, idRequest, idSacerBackend, request, countNrFound,
                        countNrDone);
            }

            log.atInfo().log("Fine lavorazione richiesta UUID {} in {} ms", request.getUuid(),
                    Duration.between(start, LocalDateTime.now()).toMillis());

            // final update and cleanup
            osService.updateOsRequest(idRequest, RequestCnts.State.FINISHED, Optional.empty(),
                    Optional.of(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
                    Optional.of(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        } finally {
            // close execution
            executor.close();
            vertx.close();
        }
    }

    // processa un batch di oggetti
    private void processBatch(List<Long> batch, WorkerExecutor executor, Long idRequest,
            Long idSacerBackend, RequestDto request, AtomicInteger countNrFound,
            AtomicInteger countNrDone) {
        // increment running batches
        runningBatches.incrementAndGet();
        try {
            AtomicInteger batchIdx = new AtomicInteger(1);
            // list of futures for the batch
            List<Future<Void>> futures = new ArrayList<>();

            for (Long objId : batch) {
                // increment index
                int currentBatchIdx = batchIdx.getAndIncrement();
                // use executeBlocking with Callable
                // executeBlocking is designed for blocking operations (I/O, DB calls, etc.),
                // not
                // for
                // CPU-intensive tasks.
                Future<Void> future = executor.executeBlocking(() -> {
                    // check shutdown in progress
                    if (shuttingDown) {
                        log.atWarn().log(
                                "Gracefulshutdown in corso: interrotta migrazione per {} id {}",
                                getObjType(), objId);
                        return null; // just exit the task gracefully
                    }
                    // log start
                    LocalDateTime execStart = LocalDateTime.now();
                    log.atInfo().log("Inizio migrazione {} id {} - ({} di {})", getObjType(), objId,
                            currentBatchIdx, batch.size());

                    // update counter countNrFound
                    countNrFound.incrementAndGet();

                    try {
                        // migrate execution
                        IObjectStorageResource osresource = executeMigrateViaS3(idSacerBackend,
                                objId, request.getDeleteSrc());
                        // update session vers registerd on OS
                        if (persistCompletedEnabled) {
                            osService.createOsObjectStorageOfObject(idRequest, objId,
                                    ObjectStorageCnts.State.MIGRATED, getObjType(),
                                    Optional.of(osresource.getS3Bucket()),
                                    Optional.of(osresource.getS3Key()),
                                    Optional.of(osresource.getObjBase64()),
                                    Optional.ofNullable(osresource.getSHA256()),
                                    Optional.of(ObjectStorageCnts.IntegrityType
                                            .valueOf(osresource.getS3Checksum())),
                                    Optional.empty());
                        }
                        // update counter countNrDone
                        countNrDone.incrementAndGet();

                        LocalDateTime end = LocalDateTime.now();
                        log.atInfo().log("Fine migrazione {} id {} in {} ms - ({} di {})",
                                getObjType(), objId, Duration.between(execStart, end).toMillis(),
                                currentBatchIdx, batch.size());
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
                    return null;
                });
                // Future<Void> -> returning Void
                futures.add(future);
            }
            // Wait for batch to complete before proceeding
            Future.all(futures).toCompletionStage().toCompletableFuture().join();

            // update request (count nr of session migrated correctly + founded)
            osService.updateOsRequest(idRequest, RequestCnts.State.IN_PROGRESS, Optional.empty(),
                    Optional.of(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
                    Optional.empty(), Optional.of(Long.valueOf(countNrFound.get())),
                    Optional.of(Long.valueOf(countNrDone.get())), Optional.empty(),
                    Optional.empty());
        } finally {
            // decrement running batches
            runningBatches.decrementAndGet();
        }
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

    /**
     * Metodo invocato in fase di terminazione del servizio per gestire una chiusura ordinata delle
     * migrazioni in corso.
     */
    @PreDestroy
    public void gracefulshutdown() {
        shuttingDown = true;
        log.atInfo().log("Gracefulshutdown in corso...attesa completamento batch in esecuzione.");
        while (runningBatches.get() > 0) {
            try {
                Thread.sleep(2000); // Wait for batches to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.atWarn().log(
                        "Gracefulshutdown interrotto mentre si attendevano i batch in esecuzione.",
                        e);
                break;
            }
        }
        log.atInfo().log("Tutti i batch sono completati. Gracefulshutdown concluso.");
    }

}
