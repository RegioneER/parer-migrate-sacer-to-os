/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.sip.job.predicate;

import it.eng.parer.migrate.sacer.os.beans.job.predicate.MigrateOsJobPredicate;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts.Type;
import jakarta.inject.Singleton;

@Singleton
public class MigrateOsSipRequestPredicate extends MigrateOsJobPredicate {

    /*
     * Definisce la tipologia di request gestito dal predicato al fine di permettere il run
     * dell'apposito job
     */
    @Override
    public Type requestType() {
	return RequestCnts.Type.SIP;
    }

}
