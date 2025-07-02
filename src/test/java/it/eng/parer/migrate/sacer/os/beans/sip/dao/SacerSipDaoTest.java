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