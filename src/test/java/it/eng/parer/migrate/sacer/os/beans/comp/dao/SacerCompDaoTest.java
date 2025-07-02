package it.eng.parer.migrate.sacer.os.beans.comp.dao;

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
import it.eng.parer.migrate.sacer.os.beans.comp.ISacerCompDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class SacerCompDaoTest {
    @Inject
    ISacerCompDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.comp.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @Test
    @TestTransaction
    void findIdsCompDocByIdStrutTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsCompDoc(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsCompDocByIdDocTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdDoc(4896792L);

	assertEquals(1L, dao.findIdsCompDoc(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsCompDocByIdUdTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(3736066L);

	assertEquals(1L, dao.findIdsCompDoc(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsCompDocByIdComDocTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdCompDoc(5199798L);

	assertEquals(1L, dao.findIdsCompDoc(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkCompTest() {
	//
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkComp(tenant, bucketName,
		UUID.randomUUID().toString(), 16594053858L, 1L));
    }

    @Test
    @TestTransaction
    void findCompDocByIdTestNoException() {
	//
	assertDoesNotThrow(() -> dao.findCompDocById(4413L));
    }

    @Test
    @TestTransaction
    void findCompDocByIdTestWithException() {
	//
	assertThrows(AppMigrateOsS3Exception.class, () -> dao.findCompDocById(null));
    }

    @Test
    @TestTransaction
    void deleteBlContenutoCompTestNoException() {
	//
	assertDoesNotThrow(() -> dao.deleteBlContenutoComp(4L));
    }

    @Test
    @TestTransaction
    void deleteBlContenutoCompTestWithException() throws AppMigrateOsDeleteSrcException {
	//
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlContenutoComp(Long.MIN_VALUE));
    }

    @Test
    @TestTransaction
    void getIdUnitaDocByIdCompDocTestNoException() {
	assertDoesNotThrow(() -> dao.getIdUnitaDocByIdCompDoc(16594053858L));
    }

    @Test
    @TestTransaction
    void getIdDocByIdCompDocTestNoException() {
	assertDoesNotThrow(() -> dao.getIdDocByIdCompDoc(16594053858L));
    }

    @Test
    @TestTransaction
    void getIdUnitaDocByIdCompDocTestWithException() {
	assertThrows(AppMigrateOsS3Exception.class, () -> dao.getIdUnitaDocByIdCompDoc(null));
    }

    @Test
    @TestTransaction
    void getIdDocByIdCompDocTestWithException() {
	assertThrows(AppMigrateOsS3Exception.class, () -> dao.getIdDocByIdCompDoc(null));
    }

    @Test
    @TestTransaction
    void findIdsCompDocOnViewAsStreamTest() {
	//
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsCompDocOnView(filter).count());
	// assertEquals(8L,
	// dao.findIdsSessVersUdDocChiusaOkAsStream(filter).findFirst().get().getOrgStrut());
	// assertEquals(Arrays.asList(8368783475L, 8266783476L),
	// dao.findVrsSessVersUdDocChiusaOkAsStream(filter).map(VrsSessioneVers::getIdSessioneVers).toList());
	// assertNotEquals(Arrays.asList(9547783583L, 3873783586L),
	// dao.findVrsSessVersUdDocChiusaOkAsStream(filter).map(VrsSessioneVers::getIdSessioneVers).toList());
    }

    @Test
    @TestTransaction
    void findIdsCompDocOnViewAsStreamTestWithNoResult() {
	//
	FilterDto filter = new FilterDto();

	assertEquals(0L, dao.findIdsCompDocOnView(filter).count());
	// assertEquals(8L,
	// dao.findIdsSessVersUdDocChiusaOkAsStream(filter).findFirst().get().getOrgStrut());
	// assertEquals(Arrays.asList(8368783475L, 8266783476L),
	// dao.findVrsSessVersUdDocChiusaOkAsStream(filter).map(VrsSessioneVers::getIdSessioneVers).toList());
	// assertNotEquals(Arrays.asList(9547783583L, 3873783586L),
	// dao.findVrsSessVersUdDocChiusaOkAsStream(filter).map(VrsSessioneVers::getIdSessioneVers).toList());
    }
}