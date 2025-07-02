package it.eng.parer.migrate.sacer.os.beans.datispecvers.job.predicate;

import it.eng.parer.migrate.sacer.os.beans.job.predicate.MigrateOsJobPredicate;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts.Type;
import jakarta.inject.Singleton;

@Singleton
public class MigrateOsDatiSpecVersRequetPredicate extends MigrateOsJobPredicate {
    /*
     * Definisce la tipologia di request gestito dal predicato al fine di permettere il run
     * dell'apposito job
     */
    @Override
    public Type requestType() {
	return RequestCnts.Type.DATI_SPEC_VERS;
    }
}
