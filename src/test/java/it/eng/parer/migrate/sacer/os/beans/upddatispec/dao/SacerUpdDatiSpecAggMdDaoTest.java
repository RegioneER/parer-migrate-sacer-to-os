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

package it.eng.parer.migrate.sacer.os.beans.upddatispec.dao;

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
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.ISacerUpdDatiSpecAggMdDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUnitaDocCnts.TiEntitaAroUpdDatiSpecUnitaDoc;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerUpdDatiSpecAggMdDaoTest {
    @Inject
    ISacerUpdDatiSpecAggMdDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void findIdsUpdDatiSpecAggMdByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsUpdDatiSpecAggMd(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsUpdDatiSpecAggMdByIdUdTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(77924080627L);

	assertEquals(1L, dao.findIdsUpdDatiSpecAggMd(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkUpdDatiSpecAggMdTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkUpdDatiSpecAggMd(tenant, bucketName,
		UUID.randomUUID().toString(), 98212644L,
		new DatiSpecLinkOsKeyMap(16501L, TiEntitaAroUpdDatiSpecUnitaDoc.UPD_UNI_DOC.name()),
		1L));
    }

    @Test
    @TestTransaction
    void findUpdDatiSpecAggMdByIdUpdUnitaDocTestNoException() {
	assertDoesNotThrow(() -> dao.findUpdDatiSpecAggMdByIdUpdUnitaDoc(16501L));
    }

    @Test
    @TestTransaction
    void deleteBlUpdDatiSpecAggMdTestNoException() {
	List<Long> list = new ArrayList<Long>();
	list.add(94151L);
	list.add(576545L);
	assertDoesNotThrow(() -> dao.deleteBlUpdDatiSpecAggMd(list));
    }

    @Test
    @TestTransaction
    void deleteBlUpdDatiSpecAggMdTestWithException() {
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlUpdDatiSpecAggMd(Arrays.asList(Long.MIN_VALUE)));
    }

    @Test
    @TestTransaction
    void deleteBlUpdDatiSpecAggMdTestWithEmptyList() {
	assertDoesNotThrow(() -> dao.deleteBlUpdDatiSpecAggMd(new ArrayList<>()));
    }
}
