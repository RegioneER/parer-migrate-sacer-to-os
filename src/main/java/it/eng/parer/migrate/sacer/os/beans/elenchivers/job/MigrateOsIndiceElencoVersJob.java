package it.eng.parer.migrate.sacer.os.beans.elenchivers.job;

import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.getHostname;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.MDC_LOG_UUID;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsService;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.IMigrateOsIndiceElencoVersService;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.job.predicate.MigrateOsIndiceElencoVersRequestPredicate;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsIndiceElencoVersJob {
    private static final Logger log = LoggerFactory.getLogger(MigrateOsIndiceElencoVersJob.class);

    @Inject
    IMigrateOsService osBaseService;

    @Inject
    IMigrateOsIndiceElencoVersService osElvElencoVersService;

    @Scheduled(cron = "{job.indelv.cron}", concurrentExecution = ConcurrentExecution.PROCEED, skipExecutionIf = MigrateOsIndiceElencoVersRequestPredicate.class)
    void processRegisterReq() {
	// 1. get the request on state WAITING (only ONE)
	Requests osElvElencoVersRequest = osBaseService
		.findAndLockOsRequestBeforeStart(RequestCnts.Type.INDICE_ELENCO_VERS);
	// 1.1. init MDC
	MDC.put(MDC_LOG_UUID, osElvElencoVersRequest.getUuid());

	try {
	    LocalDateTime start = LocalDateTime.now();
	    log.atInfo().log("Inizio lavorazione richiesta UUID {}",
		    osElvElencoVersRequest.getUuid());
	    //
	    // 2 update request
	    osBaseService.updateOsRequest(osElvElencoVersRequest.getIdRequest(),
		    RequestCnts.State.STARTED,
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		    getHostname());

	    // 3. find by filter AroCompDoc (as stream)
	    osElvElencoVersService.processMigrationIndiceElencoVersUdFromRequest(
		    osElvElencoVersRequest.getIdRequest());
	    //
	    //
	    LocalDateTime end = LocalDateTime.now();
	    log.atInfo().log("Fine lavorazione richiesta UUID {} in {} ms",
		    osElvElencoVersRequest.getUuid(), Duration.between(start, end).toMillis());
	} catch (Exception e) {
	    // update request with local error
	    osBaseService.updateOsRequest(osElvElencoVersRequest.getIdRequest(),
		    RequestCnts.State.ERROR, Optional.empty(),
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.empty(), Optional.empty(),
		    Optional.of(ExceptionUtils.getStackTrace(e)), Optional.empty());

	    throw e;
	}
    }
}
