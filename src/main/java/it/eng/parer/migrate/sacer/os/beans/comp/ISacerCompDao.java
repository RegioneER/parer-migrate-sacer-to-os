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

package it.eng.parer.migrate.sacer.os.beans.comp;

import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroCompDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;

public interface ISacerCompDao {

    Stream<Long> findIdsCompDoc(FilterDto filter);

    Stream<Long> findIdsCompDocOnView(FilterDto filter);

    void saveObjectStorageLinkComp(String tenant, String bucket, String key, Long idCompDoc,
	    Long idBackend);

    AroCompDoc findCompDocById(Long idCompDoc) throws AppMigrateOsS3Exception;

    void deleteBlContenutoComp(Long idContenutoComp) throws AppMigrateOsDeleteSrcException;

    Long getIdUnitaDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception;

    Long getIdDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception;

    VrsSessioneVers findVrsSessioneVersByIdStrutAndIdCompDoc(Long idStrut, Long idCompDoc)
	    throws AppMigrateOsS3Exception;
}
