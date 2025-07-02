package it.eng.parer.migrate.sacer.os.beans.aip.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.beans.aip.ISacerAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerAipDaoTest {
    @Inject
    ISacerAipDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.aip.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @Test
    @TestTransaction
    void findIdsVerAipByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsVerAip(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsVerAipByIdUdTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(56972L);

	assertEquals(1L, dao.findIdsVerAip(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkAipTest() {
	assertDoesNotThrow(
		() -> dao.saveObjectStorageLinkAip(tenant, bucketName, UUID.randomUUID().toString(),
			1L, BigDecimal.valueOf(Year.now().getValue()), 100095989L, 1L));
    }

    @Test
    @TestTransaction
    void findFileVerIndiceAipUdByIdVerIndiceAipTestNoException() {
	assertDoesNotThrow(
		() -> dao.findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(1000495303L));
    }

    @Test
    @TestTransaction
    void findFileVerIndiceAipUdByIdVerIndiceAipTestWithException() {
	//
	assertThrows(AppMigrateOsS3Exception.class,
		() -> dao.findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void deleteBlFileVerIndiceAipTestNoException() {
	assertDoesNotThrow(() -> dao.deleteBlFileVerIndiceAip(100046577L));
    }

    @Test
    @TestTransaction
    void deleteBlFileVerIndiceAipTestWithException() {
	//
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlFileVerIndiceAip(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void findFileVerIndiceAipUdByIdStrutTestNoResult() {
	FilterDto filter = new FilterDto();
	filter.setIdStrut(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsVerAip(filter).count());
    }

    @Test
    @TestTransaction
    void findFileVerIndiceAipUdByIdVerIndiceAipTestNoResult() {
	FilterDto filter = new FilterDto();
	filter.setIdVerIndiceAip(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsVerAip(filter).count());
    }

    @Test
    @TestTransaction
    void findFileVerIndiceAipUdWrongRowLimitTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(-1L); // invalid row limit

	assertThrows(AppGenericRuntimeException.class, () -> dao.findIdsVerAip(filter));
    }
}
