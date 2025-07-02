package it.eng.parer.migrate.sacer.os.beans.job.predicate;

import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;

public interface IMigrateOsJobPredicate<T extends Enum<RequestCnts.Type>> {

    T requestType();

}