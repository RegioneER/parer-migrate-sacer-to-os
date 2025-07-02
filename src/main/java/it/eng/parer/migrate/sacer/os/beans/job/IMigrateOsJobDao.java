package it.eng.parer.migrate.sacer.os.beans.job;

import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;

public interface IMigrateOsJobDao {

    boolean exceedNrRequestRunningPerHost(Integer hostMaxReq, String hostname);

    boolean notExitsRequestRegisteredByType(RequestCnts.Type type);
}