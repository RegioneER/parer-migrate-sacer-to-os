/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.beans.sip.job;

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
import it.eng.parer.migrate.sacer.os.beans.sip.IMigrateOsSipService;
import it.eng.parer.migrate.sacer.os.beans.sip.job.predicate.MigrateOsSipRequestPredicate;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsSipJob {

    private static final Logger log = LoggerFactory.getLogger(MigrateOsSipJob.class);

    @Inject
    IMigrateOsService osBaseService;

    @Inject
    IMigrateOsSipService osSipService;

    @Scheduled(cron = "{job.sip.cron}", concurrentExecution = ConcurrentExecution.PROCEED, skipExecutionIf = MigrateOsSipRequestPredicate.class)
    void processRegisterReq() {
	// 1. get the request on state WAITING (only ONE)
	Requests osSipRequest = osBaseService.findAndLockOsRequestBeforeStart(RequestCnts.Type.SIP);
	// 1.1. init MDC
	MDC.put(MDC_LOG_UUID, osSipRequest.getUuid());

	try {
	    LocalDateTime start = LocalDateTime.now();
	    log.atInfo().log("Inizio lavorazione richiesta UUID {}", osSipRequest.getUuid());
	    //
	    // 2 update request
	    osBaseService.updateOsRequest(osSipRequest.getIdRequest(), RequestCnts.State.STARTED,
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.of(
			    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime()),
		    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		    getHostname());

	    // 3. find by filter VrsSessioneVers (as stream)
	    osSipService.processMigrationSipFromRequest(osSipRequest.getIdRequest());
	    //
	    //
	    LocalDateTime end = LocalDateTime.now();
	    log.atInfo().log("Fine lavorazione richiesta UUID {} in {} ms", osSipRequest.getUuid(),
		    Duration.between(start, end).toMillis());
	} catch (Exception e) {
	    // update request with local error
	    osBaseService.updateOsRequest(osSipRequest.getIdRequest(), RequestCnts.State.ERROR,
		    Optional.empty(),
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
