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
package it.eng.parer.migrate.sacer.os.beans.job.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.getHostname;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobDao;
import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobService;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsJobService implements IMigrateOsJobService {

    private static final Logger log = LoggerFactory.getLogger(MigrateOsJobService.class);

    @ConfigProperty(name = "job.max-execution-per-host")
    Integer hostMaxReq;

    @Inject
    IMigrateOsJobDao osJobDao;

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public boolean testJobskipExecutionByReqType(RequestCnts.Type type) {
	try {
	    // 1. check max request by host
	    boolean testNrRequest = false;
	    if (hostMaxReq > 0) {
		testNrRequest = osJobDao.exceedNrRequestRunningPerHost(hostMaxReq,
			getHostname().orElse(StringUtils.EMPTY));
	    }
	    // 2. check new request REGISTERED exists
	    boolean testNotExistReqRegistered = osJobDao.notExitsRequestRegisteredByType(type);
	    return testNrRequest || testNotExistReqRegistered;
	} catch (Exception e) {
	    log.atError().log("Errore generico", e);
	    return true; // default skip
	}
    }
}
