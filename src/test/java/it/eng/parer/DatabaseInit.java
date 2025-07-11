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

package it.eng.parer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Dependent
@Transactional(Transactional.TxType.MANDATORY)
public class DatabaseInit {

    private static final String SCRIPT_PATH = "/h2/";

    @Inject
    EntityManager em;

    public void insertRequest() {
	exceInsertScript("request.sql");
    }

    public void insertRequestWithExistingUuid() {
	exceInsertScript("request2.sql");
    }

    public void insertRequestAllRunning() {
	exceInsertScript("request_all_running.sql");
    }

    public void insertFilter() {
	exceInsertScript("filter.sql");
    }

    public void insertObjectStorage() {
	insertRequest();
	insertFilter();
	exceInsertScript("os.sql");
    }

    private int exceInsertScript(String filename) {
	try {
	    final String sql = IOUtils.toString(
		    this.getClass().getResourceAsStream(SCRIPT_PATH + filename),
		    StandardCharsets.UTF_8);
	    Query query = em.createNativeQuery(sql);
	    return query.executeUpdate();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
