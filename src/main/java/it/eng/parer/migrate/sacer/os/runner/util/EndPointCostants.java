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
package it.eng.parer.migrate.sacer.os.runner.util;

public class EndPointCostants {

    private EndPointCostants() {
	throw new IllegalStateException("Utility class");
    }

    public static final String URL_ADMIN_BASE = "/admin";

    public static final String URL_API_BASE = "/api/os";

    public static final String RESOURCE_SIPMIGRATE = "/sip/migrate";

    public static final String RESOURCE_REQUESTS = "/requests";

    public static final String RESOURCE_REQUEST = "/request";

    public static final String RESOURCE_COMPMIGRATE = "/comp/migrate";

    public static final String RESOURCE_ELENCOVERSAIPMIGRATE = "/elvaip/migrate";

    public static final String RESOURCE_AIPMIGRATE = "/aip/migrate";

    public static final String RESOURCE_AIPSERIEMIGRATE = "/aipserie/migrate";

    public static final String RESOURCE_UPDUDMIGRATE = "/sipupdud/migrate";

    public static final String RESOURCE_INDICEELENCOVERSMIGRATE = "/indiceelv/migrate";

    public static final String RESOURCE_UPDDATISPECINIMIGRATE = "/upddatispecini/migrate";

    public static final String RESOURCE_DATISPECVERSMIGRATE = "/datispecvers/migrate";

    //

    public static final String URL_POST_OS_REQ_MIGRATE = URL_API_BASE + RESOURCE_SIPMIGRATE;

    public static final String URL_GET_OS_REQS_REQUEST = URL_API_BASE + RESOURCE_REQUESTS;

    public static final String URL_GET_OS_REQ_REQUEST = URL_API_BASE + RESOURCE_REQUEST;

    public static final String URL_POST_OS_COMP_REQUEST = URL_API_BASE + RESOURCE_COMPMIGRATE;

    public static final String URL_POST_OS_ELENCOVERSAIP_REQUEST = URL_API_BASE
	    + RESOURCE_ELENCOVERSAIPMIGRATE;

    public static final String URL_POST_OS_AIP_REQUEST = URL_API_BASE + RESOURCE_AIPMIGRATE;

    public static final String URL_POST_OS_AIPSERIE_REQUEST = URL_API_BASE
	    + RESOURCE_AIPSERIEMIGRATE;

    public static final String URL_POST_OS_UPDUD_REQUEST = URL_API_BASE + RESOURCE_UPDUDMIGRATE;

    public static final String URL_POST_OS_INDICEELENCOVERS_REQUEST = URL_API_BASE
	    + RESOURCE_INDICEELENCOVERSMIGRATE;

    public static final String RESOURCE_INFOS = "/infos";

    public static final String URL_GET_INFOS = URL_ADMIN_BASE + RESOURCE_INFOS;

    public static final String MDC_LOG_UUID = "log_uuid";

    public enum OsRequestOrderByCol {
	DT_INSERT, DT_LAST_UPDATE
    }

    public enum OsRequestOrderByTo {
	ASC, DESC
    }

}
