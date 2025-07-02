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

/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.sip;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsXmlDatiSessioneVers;

public interface ISacerSipDao {

    /**
     *
     * @param filter filtro applicato
     *
     * @return lista pk
     */
    Stream<Long> findIdsSessVersUdDocChiusaOk(FilterDto filter);

    void saveObjectStorageLinkSipUd(String tenant, String bucket, String key, Long idUnitaDoc,
	    Long idSacerBackend);

    void saveObjectStorageLinkSipDoc(String tenant, String bucket, String key, Long idDoc,
	    Long idSacerBackend);

    /**
     * Restituisce la lista di VrsXmlDatiSessioneVers data la sessione di versamento
     *
     * @param idSessioneVers id sessione versamento
     *
     * @return lista resultset con sessioni identificate
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     *
     */
    List<VrsXmlDatiSessioneVers> findAllXmlDatiSessioneVersByIdSessVers(Long idSessioneVers)
	    throws AppMigrateOsS3Exception;

    VrsSessioneVers findVrsSessioneVersById(Long idSessioneVers) throws AppMigrateOsS3Exception;

    void deleteBlXmlOnVrsXmlDatiSessioneVers(List<Long> xmlDatiSessioneVersIds)
	    throws AppMigrateOsDeleteSrcException;

}
