/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.job;

import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;

public interface IMigrateOsJobService {

    /**
     * Verifica presenza di richieste registrate per tipologia e (se attivo) per numero massimo
     * richieste gestite su singolo host.
     *
     *
     * @return true = job skip / false = esegui job
     *
     */
    boolean testJobskipExecutionByReqType(RequestCnts.Type type);

}