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

package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.ISacerSipAggMetadatiDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerSipAggMetadatiDaoTest {
    @Inject
    ISacerSipAggMetadatiDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void findIdsUpdUnitaDocByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsUpdUnitaDoc(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsUpdUnitaDocByIdUdTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(53310L);

	assertEquals(1L, dao.findIdsUpdUnitaDoc(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkSipUdAggMdTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkSipUdAggMd(tenant, bucketName,
		UUID.randomUUID().toString(), 98212644L, 1L, 1L));
    }

    @Test
    @TestTransaction
    void findAroUpdUnitaDocByIdTestNoException() {
	assertDoesNotThrow(() -> dao.findAroUpdUnitaDocById(98212644L));
    }

    @Test
    @TestTransaction
    void deleteBlXmlOnAroXmlUpdUnitaDocTestNoException() {
	List<Long> list = new ArrayList<Long>();
	list.add(10231L);
	list.add(26772L);
	assertDoesNotThrow(() -> dao.deleteBlXmlOnAroXmlUpdUnitaDoc(list));
    }

    @Test
    @TestTransaction
    void deleteBlXmlOnAroXmlUpdUnitaDocTestWithException() {
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlXmlOnAroXmlUpdUnitaDoc(Arrays.asList(Long.MIN_VALUE)));
    }

    @Test
    @TestTransaction
    void findIdsUpdUnitaDocAsStreamTestEmptyResult() {
	FilterDto filter = new FilterDto();
	filter.setIdStrut(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsUpdUnitaDoc(filter).count());
    }
}
