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

package it.eng.parer.migrate.sacer.os.beans.aipserie.dao;

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
import it.eng.parer.migrate.sacer.os.beans.aipserie.ISacerAipSerieDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.SerFileVerSerieCnts;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerAipSerieDaoTest {
    @Inject
    ISacerAipSerieDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.aipserie.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @Test
    @TestTransaction
    void findIdsVerSerieByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsVerSerie(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsVerSerieByIdVerSerieTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdVerSerie(953921L);

	assertEquals(1L, dao.findIdsVerSerie(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkAipSerieIX_AIP_UNISINCROTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkAipSerie(tenant, bucketName,
		UUID.randomUUID().toString(), 8L, 7425244L,
		SerFileVerSerieCnts.TiFileVerSerie.IX_AIP_UNISINCRO, 1L));
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkAipSerieIX_AIP_UNISINCRO_FIRMATOTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkAipSerie(tenant, bucketName,
		UUID.randomUUID().toString(), 8L, 7425244L,
		SerFileVerSerieCnts.TiFileVerSerie.IX_AIP_UNISINCRO_FIRMATO, 1L));
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkAipSerieMARCA_IX_AIP_UNISINCROTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkAipSerie(tenant, bucketName,
		UUID.randomUUID().toString(), 8L, 7425244L,
		SerFileVerSerieCnts.TiFileVerSerie.MARCA_IX_AIP_UNISINCRO, 1L));
    }

    @Test
    @TestTransaction
    void findSerSerieAndIndiceAndFileAipByIdVerSerieTestNoException() {
	assertDoesNotThrow(() -> dao.findSerSerieAndIndiceAndFileAipByIdFileVerSerie(23971L));
    }

    @Test
    @TestTransaction
    void findSerSerieAndIndiceAndFileAipByIdVerSerieWithException() {
	//
	assertThrows(AppMigrateOsS3Exception.class,
		() -> dao.findSerSerieAndIndiceAndFileAipByIdFileVerSerie(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void deleteBlFileVerSerieTestNoException() {
	assertDoesNotThrow(() -> dao.deleteBlFileVerSerie(9747167L));
    }

    @Test
    @TestTransaction
    void deleteBlFileVerSerieTestWithException() {
	//
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlFileVerSerie(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void findIdsVerSerieAsStreamByIdStrutTestNoResult() {
	FilterDto filter = new FilterDto();
	filter.setIdStrut(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsVerSerie(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsVerSerieAsStreamByIdVerSerieTestNoResult() {
	FilterDto filter = new FilterDto();
	filter.setIdVerSerie(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsVerSerie(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsVerSerieAsStreamWrongRowLimitTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(-1L); // invalid row limit

	assertThrows(AppGenericRuntimeException.class, () -> dao.findIdsVerSerie(filter));
    }

}
