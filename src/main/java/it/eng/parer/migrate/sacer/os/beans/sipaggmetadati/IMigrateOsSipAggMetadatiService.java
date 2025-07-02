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

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsSipAggMetadatiService {
    /**
     *
     * Ricerca le sessioni di versamento con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationSipFromRequest(Long idRequest);

    /**
     * Registra il tipo di richiesta SIP
     *
     * @param osSipRequests richiesta/e di migrazione
     *
     * @return lista delle richieste registrate
     */
    List<RequestDto> registerMigrationSipRequest(List<MigrateRequest> osSipRequests);
}
