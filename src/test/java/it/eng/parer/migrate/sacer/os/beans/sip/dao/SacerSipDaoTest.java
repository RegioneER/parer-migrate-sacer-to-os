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

package it.eng.parer.migrate.sacer.os.beans.sip.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.sip.ISacerSipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerSipDaoTest {

    @Inject
    ISacerSipDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.sip.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void findVrsSessioneVersByIdStrutTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsSessVersUdDocChiusaOk(filter).count());
    }

    @Test
    @TestTransaction
    void findVrsSessioneVersByIdUdTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(53310L);

	assertEquals(1L, dao.findIdsSessVersUdDocChiusaOk(filter).count());
    }

    @Test
    @TestTransaction
    void findVrsSessioneVersByIdDocTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdDoc(120301L);

	assertEquals(1L, dao.findIdsSessVersUdDocChiusaOk(filter).count());
    }

    @Test
    @TestTransaction
    void findVrsSessioneVersByIdVrsSessioneVersTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdSessioneVers(3L);

	assertEquals(1L, dao.findIdsSessVersUdDocChiusaOk(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkSipUdTest() {
	//
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkSipUd(tenant, bucketName,
		UUID.randomUUID().toString(), 74597L, 1L));
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkSipDocTest() {
	//
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkSipDoc(tenant, bucketName,
		UUID.randomUUID().toString(), 64131509520L, 1L));
    }

    @Test
    @TestTransaction
    void findVrsSessioneVersByIdTest() {
	//
	assertDoesNotThrow(() -> dao.findVrsSessioneVersById(99996704059L));
    }

    @Test
    @TestTransaction
    void deleteBlXmlOnVrsXmlDatiSessioneVersTestNoException() {
	//
	assertDoesNotThrow(() -> dao.deleteBlXmlOnVrsXmlDatiSessioneVers(Arrays.asList(5L)));
    }

    @Test
    @TestTransaction
    void deleteBlXmlOnVrsXmlDatiSessioneVersTestWithException() {
	//
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlXmlOnVrsXmlDatiSessioneVers(Arrays.asList(Long.MIN_VALUE)));
    }

}
