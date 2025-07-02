/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.job.predicate;

import io.quarkus.scheduler.Scheduled.SkipPredicate;
import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobService;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Inject;

public abstract class MigrateOsJobPredicate
	implements SkipPredicate, IMigrateOsJobPredicate<RequestCnts.Type> {

    @Inject
    IMigrateOsJobService jobService;

    @Override
    public boolean test(ScheduledExecution execution) {
	return jobService.testJobskipExecutionByReqType(requestType());
    }

}
