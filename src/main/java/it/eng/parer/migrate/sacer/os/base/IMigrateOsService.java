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

package it.eng.parer.migrate.sacer.os.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.IntegrityType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.State;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.ObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;

public interface IMigrateOsService {

    /**
     * Verifica presenza di richieste registrate (ordinate per data inserimento)
     *
     *
     * @return true se richiesta individuata (ed aggiornata), false altrimenti
     *
     * @throws AppGenericRuntimeException eccezione generica
     */
    boolean testJobskipExecution(RequestCnts.Type type);

    /**
     * Aggiornamento della richiesta
     *
     * @param idRequest    pk della richiesta
     * @param state        stato
     * @param dtStart      data inizio
     * @param dtLastUpdate data aggiornamento
     * @param dtFinish     data fine
     * @param nrFounded    nr. di elementi individuati
     * @param nrDone       nr. di elementi migrati
     * @param errorDetail  eventuale errore
     */
    void updateOsRequest(Long idRequest, RequestCnts.State state, Optional<LocalDateTime> dtStart,
	    Optional<LocalDateTime> dtLastUpdate, Optional<LocalDateTime> dtFinish,
	    Optional<Long> nrFounded, Optional<Long> nrDone, Optional<String> errorDetail,
	    Optional<String> hostname);

    /**
     * Ricerca request prese in carico {@link RequestCnts.State#WAITING}
     *
     * @param type tipologia oggetto
     *
     * @return oggetto request
     */
    Requests findAndLockOsRequestBeforeStart(RequestCnts.Type type);

    List<RequestDto> findOsRequests(String state, String type, LocalDate dtstart,
	    LocalDate dtfinish, String orderbycol, String orderbyto, Integer maxresult);

    RequestDto findOsRequestByUuid(final String uuid);

    /**
     * Registrazione richiesta per tipologia oggetto da migrare
     *
     * @param osSipRequests lista richiesta/e
     * @param type          tipologia oggetto da migrare
     *
     * @return lista richieste
     */
    List<RequestDto> registerOsRequestByType(List<MigrateRequest> osSipRequests,
	    RequestCnts.Type type);

    /**
     * Crezione dell'oggetto migrato / o meno
     *
     * @param idRequest          pk della richiesta
     * @param pkObject           pk dell'oggetto migrato
     * @param state              stato migrazione
     * @param type               tipo pk oggetto migrato
     * @param bucketName         nome bucket s3
     * @param key                chiave s3
     * @param objBase64          base64 file migrato
     * @param s3checksum         checksum (SHA) se abilitato
     * @param checksumlAlghoritm tipologia check di integrità (MD5 vs SHA)
     * @param errorDetail        eventuale stacktrace con errore
     *
     * @return entità creata
     */
    ObjectStorage createOsObjectStorageOfObject(Long idRequest, Long pkObject, State state,
	    ObjectType type, Optional<String> bucketName, Optional<String> key,
	    Optional<String> objBase64, Optional<String> s3checksum,
	    Optional<IntegrityType> integrityType, Optional<String> errorDetail);

    /**
     * Recupero del Filter a partire dall'identificativo della request
     *
     * @param idRequest pk della richiesta
     *
     * @return dto request
     */
    RequestDto getRequestById(final Long idRequest);

}
