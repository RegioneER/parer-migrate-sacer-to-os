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

package it.eng.parer.migrate.sacer.os.beans.aip.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.aip.IMigrateOsAipService;
import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsAipServiceTest {
    @Inject
    IMigrateOsAipService service;

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    void registerRequest_ok() {

	MigrateRequest request = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dtos = assertDoesNotThrow(
		() -> service.registerMigrationAipRequest(Arrays.asList(request)));
	assertTrue(!dtos.isEmpty());
	assertNotNull(dtos.get(0));
    }

    @Test
    @TestTransaction
    void registerRequest_ko() {

	MigrateRequest request1 = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dto1 = assertDoesNotThrow(
		() -> service.registerMigrationAipRequest(Arrays.asList(request1)));
	assertTrue(!dto1.isEmpty());
	assertNotNull(dto1.get(0));

	MigrateRequest request2 = new MigrateRequest(null, null, null, null, null, null, 1L, null,
		null, 2023, 1L, true);
	List<RequestDto> dto2 = assertDoesNotThrow(
		() -> service.registerMigrationAipRequest(Arrays.asList(request2)));
	assertTrue(!dto2.isEmpty());
	assertNotNull(dto2.get(0));
	assertNotNull(dto2.get(0).getError());
    }

    @Test
    @TestTransaction
    @Disabled("Problema legato a transaction innestate (nella prima l'entity è recuperata, nella nuova transaction aperta "
	    + "non sempre più presente -> molto probabilmente si innesca un meccanismo di roll-back")
    void processMigration_ok() {
	databaseInit.insertRequest();
	databaseInit.insertFilter();

	assertDoesNotThrow(() -> service.processMigrationAipFromRequest(1L));

    }

    @Test
    @TestTransaction
    void processMigration_ko() {
	databaseInit.insertRequest();
	databaseInit.insertFilter();

	assertThrows(AppGenericRuntimeException.class,
		() -> service.processMigrationAipFromRequest(null));

    }

    @Test
    @TestTransaction
    void registerRequest_invalidData() {
	MigrateRequest request1 = new MigrateRequest(null, null, null, null, 1L, null, null, null,
		null, 2023, null, true);
	MigrateRequest request2 = new MigrateRequest(null, null, null, null, 1L, null, null, null,
		null, 2023, null, true);

	assertThrows(AppBadRequestException.class,
		() -> service.registerMigrationAipRequest(Arrays.asList(request1, request2)));
    }

    @Test
    @TestTransaction
    void registerRequest_emptyList() {
	List<RequestDto> dtos = assertDoesNotThrow(
		() -> service.registerMigrationAipRequest(Arrays.asList()));
	assertTrue(dtos.isEmpty());
    }

}
