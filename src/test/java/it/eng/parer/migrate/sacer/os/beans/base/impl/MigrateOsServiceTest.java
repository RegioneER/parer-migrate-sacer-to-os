package it.eng.parer.migrate.sacer.os.beans.base.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsService;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts.Type;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsServiceTest {

    @Inject
    IMigrateOsService service;

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    void registerRequestSIP_ok() {

	MigrateRequest request = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dtos = assertDoesNotThrow(() -> service
		.registerOsRequestByType(Arrays.asList(request), RequestCnts.Type.SIP));
	assertTrue(!dtos.isEmpty());
	assertNotNull(dtos.get(0));
    }

    @Test
    @TestTransaction
    void registerRequestCOMP_ok() {

	MigrateRequest request = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dtos = assertDoesNotThrow(() -> service
		.registerOsRequestByType(Arrays.asList(request), RequestCnts.Type.COMP));
	assertTrue(!dtos.isEmpty());
	assertNotNull(dtos.get(0));
    }

    @Test
    @TestTransaction
    void registerRequest_ko() {

	MigrateRequest request1 = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dto1 = assertDoesNotThrow(() -> service
		.registerOsRequestByType(Arrays.asList(request1), RequestCnts.Type.SIP));
	assertTrue(!dto1.isEmpty());
	assertNotNull(dto1.get(0));

	MigrateRequest request2 = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dto2 = assertDoesNotThrow(() -> service
		.registerOsRequestByType(Arrays.asList(request2), RequestCnts.Type.SIP));
	assertTrue(!dto2.isEmpty());
	assertNotNull(dto2.get(0));
	assertNotNull(dto2.get(0).getError());
    }

    @Test
    @TestTransaction
    void findOsRequestByUuid_ok() {
	// add request
	databaseInit.insertRequest();
	// add filter
	databaseInit.insertFilter();

	RequestDto dto = assertDoesNotThrow(
		() -> service.findOsRequestByUuid("72dfb81-0ac8-46f6-9e00-f3f477934a9a"));
	assertNotNull(dto);
	assertNotNull(dto.getFilter());
    }

    @Test
    @TestTransaction
    void registerRequest_emptyList() {
	List<RequestDto> dtos = assertDoesNotThrow(
		() -> service.registerOsRequestByType(Arrays.asList(), Type.SIP));
	assertTrue(dtos.isEmpty());
    }

}