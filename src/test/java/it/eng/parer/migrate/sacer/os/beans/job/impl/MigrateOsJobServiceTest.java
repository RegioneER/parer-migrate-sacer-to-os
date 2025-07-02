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

package it.eng.parer.migrate.sacer.os.beans.job.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobService;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsJobServiceTest {

    @Inject
    IMigrateOsJobService service;

    @Inject
    DatabaseInit databaseInit;

    // SIP
    @Test
    @TestTransaction
    void testJobskipExecutionBySipType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.SIP));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionBySipType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.SIP));
	assertFalse(skip);
    }

    // COMP
    @Test
    @TestTransaction
    void testJobskipExecutionByCompType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.COMP));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByCompType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.COMP));
	assertFalse(skip);
    }

    // AIP

    @Test
    @TestTransaction
    void testJobskipExecutionByAipType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.AIP));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByAipType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.AIP));
	assertFalse(skip);
    }

    // AIP_SERIE

    @Test
    @TestTransaction
    void testJobskipExecutionByAipSerieType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.AIP_SERIE));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByAipSerieType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.AIP_SERIE));
	assertFalse(skip);
    }

    // ELENCO_INDICI_AIP

    @Test
    @TestTransaction
    void testJobskipExecutionByElencoIndiciAipType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.ELENCO_INDICI_AIP));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByElencoIndiciAipType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.ELENCO_INDICI_AIP));
	assertFalse(skip);
    }

    // INDICE_ELENCO_VERS

    @Test
    @TestTransaction
    void testJobskipExecutionByIndiceElencoVersType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.INDICE_ELENCO_VERS));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByIndiceElencoVersType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.INDICE_ELENCO_VERS));
	assertFalse(skip);
    }

    // SIPUPDUD

    @Test
    @TestTransaction
    void testJobskipExecutionBySipUpdUdType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.SIPUPDUD));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionBySipUpdUdType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.SIPUPDUD));
	assertFalse(skip);
    }

    // UPD_DATI_SPEC_INI

    @Test
    @TestTransaction
    void testJobskipExecutionByUpdDatiSpecIniType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.UPD_DATI_SPEC_INI));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByUpdDatiSpecIniType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.UPD_DATI_SPEC_INI));
	assertFalse(skip);
    }

    // DATI_SPEC_VERS

    @Test
    @TestTransaction
    void testJobskipExecutionByDatiSpecVersType_true() {

	databaseInit.insertRequestAllRunning();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.DATI_SPEC_VERS));
	assertTrue(skip);
    }

    @Test
    @TestTransaction
    void testJobskipExecutionByDatiSpecVersType_false() {

	databaseInit.insertRequest();

	boolean skip = assertDoesNotThrow(
		() -> service.testJobskipExecutionByReqType(RequestCnts.Type.DATI_SPEC_VERS));
	assertFalse(skip);
    }

}
