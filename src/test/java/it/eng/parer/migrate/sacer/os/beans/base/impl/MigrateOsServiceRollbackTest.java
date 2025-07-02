package it.eng.parer.migrate.sacer.os.beans.base.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsService;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.IntegrityType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsServiceRollbackTest {

    @InjectMock
    IMigrateOsService serviceMock;

    @Inject
    IMigrateOsService service;

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    void registerRequestAppGenericRuntimeException_ko() {

	MigrateRequest request = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	when(serviceMock.registerOsRequestByType(any(), any()))
		.thenThrow(appGenericRuntimeException());

	List<MigrateRequest> requests = Arrays.asList(request);
	final AppGenericRuntimeException appException = assertThrows(
		AppGenericRuntimeException.class,
		() -> service.registerOsRequestByType(requests, RequestCnts.Type.SIP),
		"Should fail throwing AppGenericRuntimeException");
	assertEquals(ErrorCategory.INTERNAL_ERROR, appException.getCategory());

	List<RequestDto> result = service.findOsRequests(null, RequestCnts.Type.SIP.name(),
		LocalDate.now(), null, null, null, null);
	assertTrue(result.isEmpty());
    }

    @Test
    @TestTransaction
    void registerRequestAppBadRequestException_ko() {

	MigrateRequest request = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	when(serviceMock.registerOsRequestByType(any(), any())).thenThrow(appBadRequestException());

	List<MigrateRequest> requests = Arrays.asList(request);
	final AppBadRequestException appException = assertThrows(AppBadRequestException.class,
		() -> service.registerOsRequestByType(requests, RequestCnts.Type.SIP),
		"Should fail throwing AppBadRequestException");
	assertEquals(ErrorCategory.VALIDATION_ERROR, appException.getCategory());

	List<RequestDto> result = service.findOsRequests(null, RequestCnts.Type.SIP.name(),
		LocalDate.now(), null, null, null, null);
	assertTrue(result.isEmpty());
    }

    @Test
    @TestTransaction
    void createOsObjectStorageOfObjectAppGenericRuntimeException_ko() {

	when(serviceMock.createOsObjectStorageOfObject(any(), any(), any(), any(), any(), any(), any(), any(), any(),
		any())).thenThrow(appGenericRuntimeException());

	final Optional<String> emptyStr = Optional.empty();
	final Optional<IntegrityType> emptyIntegrity = Optional.empty();
	final AppGenericRuntimeException appException = assertThrows(AppGenericRuntimeException.class,
		() -> service.createOsObjectStorageOfObject(1L, 1L, ObjectStorageCnts.State.MIGRATED,
			ObjectType.ARO_INDICE_AIP_UD, emptyStr, emptyStr, emptyStr,
			emptyStr, emptyIntegrity, emptyStr),
		"Should fail throwing AppGenericRuntimeException");
	assertEquals(ErrorCategory.INTERNAL_ERROR, appException.getCategory());
    }

    private AppGenericRuntimeException appGenericRuntimeException() {
	return AppGenericRuntimeException.builder().category(ErrorCategory.INTERNAL_ERROR)
		.message("Errore generico").build();
    }

    private AppBadRequestException appBadRequestException() {
	return AppBadRequestException.builder().category(ErrorCategory.VALIDATION_ERROR)
		.message("Errore generico").build();
    }
}