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

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.IntegrityType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.State;
import it.eng.parer.migrate.sacer.os.jpa.entity.ObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import jakarta.validation.Valid;

public interface IMigrateOsDao {

    void lockRequests();

    Requests createRequest(@Valid MigrateRequest osRequest, RequestCnts.Type type, String s3Tenant,
	    String s3BackendName);

    void updateRequest(Long idRequest, RequestCnts.State state, Optional<LocalDateTime> dtStart,
	    Optional<LocalDateTime> dtLastUpdate, Optional<LocalDateTime> dtFinish,
	    Optional<Long> nrFounded, Optional<Long> nrDone, Optional<String> errorDetail,
	    Optional<String> hostname);

    Requests findRequestByUuid(final String uuid);

    Requests findRequestById(Long idRequest);

    List<Requests> findRequestsByStateTypeDtStartFinish(Optional<RequestCnts.State> state,
	    Optional<LocalDate> dtstart, Optional<LocalDate> dtfinish,
	    Optional<RequestCnts.Type> type, String orderByCol, String orderByTo, int maxresult);

    List<String> checkRequestsByStateAndFilter(FilterDto filterDto, RequestCnts.Type type);

    ObjectStorage createObjectStorage(Long idRequest, Long pkObject, State state,
	    ObjectStorageCnts.ObjectType type, Optional<String> bucketName, Optional<String> key,
	    Optional<String> objBase64, Optional<String> s3checksum,
	    Optional<ObjectStorageCnts.IntegrityType> integrityType, Optional<String> errorDetail);

    void updateObjectStorage(Long idObjectStorage, State state, Optional<String> bucketName,
	    Optional<String> key, Optional<String> objBase64, Optional<String> s3checksum,
	    Optional<IntegrityType> integrityType, Optional<String> errorDetail);
}
