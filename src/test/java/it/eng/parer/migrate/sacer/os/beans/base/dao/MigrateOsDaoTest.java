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

package it.eng.parer.migrate.sacer.os.beans.base.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsDao;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppEntityNotFoundException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByCol;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByTo;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsDaoTest {

    @Inject
    IMigrateOsDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @ConfigProperty(name = "s3.sip.bucket.name")
    String sipBucketName;

    @ConfigProperty(name = "s3.comp.bucket.name")
    String compBucketName;

    @Test
    @TestTransaction
    void createRequestTest() {
	//
	MigrateRequest request = new MigrateRequest(1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, LocalDate.now(),
		2023, 1L, Boolean.FALSE);
	assertDoesNotThrow(
		() -> dao.createRequest(request, RequestCnts.Type.SIP, tenant, nmBackend));
    }

    @Test
    @TestTransaction
    void createRequestWithThrowTest() {
	//
	MigrateRequest request = new MigrateRequest();
	assertThrows(ConstraintViolationException.class,
		() -> dao.createRequest(request, RequestCnts.Type.SIP, tenant, nmBackend),
		"RequestCnts not valid (no filter)");
    }

    @Test
    @TestTransaction
    void updateRequestTest() {
	//
	databaseInit.insertRequest();
	assertDoesNotThrow(() -> dao.updateRequest(1L, RequestCnts.State.FINISHED,
		Optional.of(LocalDateTime.now()), Optional.of(LocalDateTime.now()),
		Optional.of(LocalDateTime.now()), Optional.of(1L), Optional.of(1L),
		Optional.empty(), Optional.empty()));

    }

    @Test
    @TestTransaction
    void updateRequestWithExceptionTest() {
	//
	databaseInit.insertRequest();
	assertThrows(AppGenericRuntimeException.class,
		() -> dao.updateRequest(Long.MIN_VALUE, RequestCnts.State.FINISHED,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()));

    }

    @Test
    @TestTransaction
    void findRequestByUuidTest() {
	//
	final String uuid = "72dfb81-0ac8-46f6-9e00-f3f477934a9a";

	databaseInit.insertRequest();
	assertDoesNotThrow(() -> dao.findRequestByUuid(uuid));
    }

    @Test
    @TestTransaction
    void findRequestByUuidWithAppEntityNotFoundExceptionTest() {
	//
	final String uuid = "72dfb81-0ac8-46f6-9e00-f3f477934a9a";

	assertThrows(AppEntityNotFoundException.class, () -> dao.findRequestByUuid(uuid));
    }

    @Test
    @TestTransaction
    void findRequestByUuidWithExceptionTest() {
	//
	final String uuid = "72dfb81-0ac8-46f6-9e00-f3f477934a9a";
	databaseInit.insertRequest();
	databaseInit.insertRequestWithExistingUuid();
	assertThrows(AppGenericRuntimeException.class, () -> dao.findRequestByUuid(uuid));
    }

    @Test
    @TestTransaction
    void findRequestByIdTest() {
	//
	databaseInit.insertRequest();
	assertEquals(1L, dao.findRequestById(1L).getIdRequest());
    }

    @Test
    @TestTransaction
    void findRequestByIdTestWithNotFoundException() {
	//
	assertThrows(AppEntityNotFoundException.class, () -> dao.findRequestByUuid("fake_uuid"));
    }

    @Test
    @TestTransaction
    void findRequestByIdTestWithNonUniqueException() {
	//
	databaseInit.insertRequest();
	assertThrows(AppGenericRuntimeException.class,
		() -> dao.findRequestByUuid("97d172e8-efaf-11ef-bf49-8796a3b41957"));
    }

    @Test
    @TestTransaction
    void findRequestsByStateDtStartFinishTest() {
	//
	databaseInit.insertRequest();

	assertEquals(1,
		dao.findRequestsByStateTypeDtStartFinish(Optional.of(RequestCnts.State.REGISTERED),
			Optional.empty(), Optional.empty(), Optional.of(RequestCnts.Type.SIP),
			OsRequestOrderByCol.DT_INSERT.toString(), OsRequestOrderByTo.ASC.toString(),
			1).size());

    }

    @Test
    @TestTransaction
    void findRequestsByStateDtStartFinishFilterByDtStartTest() {
	//
	databaseInit.insertRequest();

	assertEquals(1,
		dao.findRequestsByStateTypeDtStartFinish(Optional.of(RequestCnts.State.REGISTERED),
			Optional.of(LocalDate.of(2025, 2, 3)), Optional.empty(), Optional.empty(),
			OsRequestOrderByCol.DT_INSERT.toString(), OsRequestOrderByTo.ASC.toString(),
			1).size());

    }

    @Test
    @TestTransaction
    void findRequestsByStateDtStartFinishFilterByDtFinishTest() {
	//
	databaseInit.insertRequest();

	assertEquals(1,
		dao.findRequestsByStateTypeDtStartFinish(Optional.of(RequestCnts.State.REGISTERED),
			Optional.empty(), Optional.of(LocalDate.of(2025, 4, 11)), Optional.empty(),
			OsRequestOrderByCol.DT_INSERT.toString(), OsRequestOrderByTo.ASC.toString(),
			1).size());

    }

    @Test
    @TestTransaction
    void findRequestsByStateDtStartFinishWithExceptionTest() {
	//
	assertThrows(AppGenericRuntimeException.class,
		() -> dao.findRequestsByStateTypeDtStartFinish(Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), StringUtils.EMPTY, StringUtils.EMPTY,
			1));

    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATEDSessVersTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATED, ObjectStorageCnts.ObjectType.VRS_SESSIONE_VERS,
		Optional.of(sipBucketName), Optional.of("key"), Optional.of("fileBae64"),
		Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.empty()));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATION_ERRORS3SessVersTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATION_ERROR,
		ObjectStorageCnts.ObjectType.VRS_SESSIONE_VERS, Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.of("Full stack trace : Error generic")));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATION_ERRORSessVersTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATION_ERROR,
		ObjectStorageCnts.ObjectType.VRS_SESSIONE_VERS, Optional.of(sipBucketName),
		Optional.of("key"), Optional.of("fileBae64"), Optional.empty(),
		Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.of("Full stack trace : Error generic")));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATEDAroCompTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATED, ObjectStorageCnts.ObjectType.ARO_COMP_DOC,
		Optional.of(compBucketName), Optional.of("key"), Optional.of("fileBae64"),
		Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.empty()));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATION_ERRORS3AroCompTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATION_ERROR, ObjectStorageCnts.ObjectType.ARO_COMP_DOC,
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.of("Full stack trace : Error generic")));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfMIGRATION_ERRORAroComTest() {
	//
	Long idVrsSessioneVers = 10001L;
	databaseInit.insertRequest();

	assertDoesNotThrow(() -> dao.createObjectStorage(1L, idVrsSessioneVers,
		ObjectStorageCnts.State.MIGRATION_ERROR, ObjectStorageCnts.ObjectType.ARO_COMP_DOC,
		Optional.of(compBucketName), Optional.of("key"), Optional.of("fileBae64"),
		Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.of("Full stack trace : Error generic")));
    }

    @Test
    @TestTransaction
    void createOsObjectStorageGenericErrorTest() {
	//
	Long idVrsSessioneVers = 10001L;

	assertThrows(AppGenericRuntimeException.class,
		() -> dao.createObjectStorage(1L, idVrsSessioneVers,
			ObjectStorageCnts.State.MIGRATION_ERROR,
			ObjectStorageCnts.ObjectType.ARO_COMP_DOC, Optional.of(compBucketName),
			Optional.of("key"), Optional.of("fileBae64"), Optional.empty(),
			Optional.of(ObjectStorageCnts.IntegrityType.MD5),
			Optional.of("Full stack trace : Error generic")));
    }

    @Test
    @TestTransaction
    void updateOsObjectStorageFromSessVersTest() {
	//
	databaseInit.insertObjectStorage();

	assertDoesNotThrow(() -> dao.updateObjectStorage(1L, ObjectStorageCnts.State.MIGRATED,
		Optional.of(sipBucketName), Optional.of("key"), Optional.of("fileBae64"),
		Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.empty()));

    }

    @Test
    @TestTransaction
    void updateOsObjectStorageFromAroComDocTest() {
	//
	databaseInit.insertObjectStorage();

	assertDoesNotThrow(() -> dao.updateObjectStorage(2L, ObjectStorageCnts.State.MIGRATED,
		Optional.of(sipBucketName), Optional.of("key"), Optional.of("fileBae64"),
		Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
		Optional.empty()));

    }

    @Test
    @TestTransaction
    void updateOsObjectStorageFromAroComDocWithExceptionTest() {
	//
	assertThrows(AppGenericRuntimeException.class,
		() -> dao.updateObjectStorage(Long.MIN_VALUE, ObjectStorageCnts.State.MIGRATED,
			Optional.of(sipBucketName), Optional.of("key"), Optional.of("fileBae64"),
			Optional.empty(), Optional.of(ObjectStorageCnts.IntegrityType.MD5),
			Optional.empty()));

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdStrutTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdStrut(8L); // PARER_TEST
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(3, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdUdTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdUnitadoc(10L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdDocTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdDoc(100L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdSessVersTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdSessioneVers(1000L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdCompDocTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdCompDoc(10000L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByDtAperturaYYTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setDtApertura(LocalDate.of(2023, 1, 1));
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.SIP));
	assertEquals(3, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdVerSerieTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdVerSerie(1000L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.INDICE_ELENCO_VERS));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterByIdVerIndiceAipTest() {
	//
	databaseInit.insertRequest();
	databaseInit.insertFilter();
	// dto
	FilterDto dto = new FilterDto();
	dto.setIdVerIndiceAip(1000L);
	List<String> result = assertDoesNotThrow(
		() -> dao.checkRequestsByStateAndFilter(dto, RequestCnts.Type.AIP));
	assertEquals(1, result.size());

    }

    @Test
    @TestTransaction
    void checkRequestsByStateAndFilterWitchExceptionTest() {
	//
	assertThrows(AppGenericRuntimeException.class,
		() -> dao.checkRequestsByStateAndFilter(null, null));

    }

}
