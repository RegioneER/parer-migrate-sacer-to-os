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

package it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.ISacerElencoVersAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
public class SacerElencoVersAipDaoTest {
    @Inject
    ISacerElencoVersAipDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.elvaip.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void findIdsElvElencoVersByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsElvElencoVers(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsElvElencoVersByidElencoVersTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdElencoVers(29414984L);

	assertEquals(1L, dao.findIdsElvElencoVers(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkElencoIndiciAipUdTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkElencoIndiciAipUd(tenant, bucketName,
		UUID.randomUUID().toString(), 653522086L, 1L));
    }

    @Test
    @TestTransaction
    void findFileElencoVersByIdFileElencoVersTestNoException() {
	assertDoesNotThrow(() -> dao.findFileElencoVersByIdFileElencoVers(11856883L));
    }

    @Test
    @TestTransaction
    void deleteBlFileElencoVersTestNoException() {
	assertDoesNotThrow(() -> dao.deleteBlFileElencoVers(772422427L));
    }

    @Test
    @TestTransaction
    void deleteBlFileElencoVersTestWithException() {
	//
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlFileElencoVers(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void findIdsElvElencoVersAsStreamTestEmptyResult() {
	FilterDto filter = new FilterDto();
	filter.setIdStrut(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsElvElencoVers(filter).count());
    }
}
