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

package it.eng.parer.migrate.sacer.os.beans.aip;

import java.math.BigDecimal;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface ISacerAipDao {

    Stream<Long> findIdsVerAip(FilterDto filter);

    void saveObjectStorageLinkAip(String tenant, String bucket, String key, Long idSubStrut,
	    BigDecimal aaKeyUnitaDoc, Long idVerIndiceAip, Long idBackend);

    Object[] findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(Long idVerIndiceAip)
	    throws AppMigrateOsS3Exception;

    void deleteBlFileVerIndiceAip(Long idFileVerIndiceAIp) throws AppMigrateOsDeleteSrcException;

}
