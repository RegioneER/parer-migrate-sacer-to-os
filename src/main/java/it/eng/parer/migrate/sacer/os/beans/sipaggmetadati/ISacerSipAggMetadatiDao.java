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

package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUpdUnitaDoc;

public interface ISacerSipAggMetadatiDao {
    /**
     *
     * @param filter filtro applicato
     *
     * @return lista pk
     */
    Stream<Long> findIdsUpdUnitaDoc(FilterDto filter);

    AroUpdUnitaDoc findAroUpdUnitaDocById(Long idUpdUnitaDoc) throws AppMigrateOsS3Exception;

    void deleteBlXmlOnAroXmlUpdUnitaDoc(List<Long> xmlUpdUnitaDocIds)
	    throws AppMigrateOsDeleteSrcException;

    void saveObjectStorageLinkSipUdAggMd(String tenant, String bucket, String key,
	    long idUpdUnitaDoc, long idStrut, Long idSacerBackend);

    List<AroXmlUpdUnitaDoc> findAllXmlUpdUnitaDocByIdUpdUnitaDoc(Long idUpdUnitaDoc)
	    throws AppMigrateOsS3Exception;

}
