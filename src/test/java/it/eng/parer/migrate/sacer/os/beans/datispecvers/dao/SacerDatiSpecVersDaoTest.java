package it.eng.parer.migrate.sacer.os.beans.datispecvers.dao;

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
import it.eng.parer.migrate.sacer.os.beans.datispecvers.ISacerDatiSpecVersDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroVersIniDatiSpecObjectStorageCnts.TiEntitaSacerAroVersIniDatiSpecOs;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
public class SacerDatiSpecVersDaoTest {
    @Inject
    ISacerDatiSpecVersDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.sipupdud.bucket.name")
    String bucketName;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void findIdsDatiSpecVersByIdStrutTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdStrut(8L); // fixed (PARER_TEST)

	assertEquals(1L, dao.findIdsDatiSpecVers(filter).count());
    }

    @Test
    @TestTransaction
    void findIdsDatiSpecVersByIdUdTest() {
	FilterDto filter = new FilterDto();
	filter.setRowlimit(1L); // fixed
	filter.setIdUnitadoc(3735063L);

	assertEquals(1L, dao.findIdsDatiSpecVers(filter).count());
    }

    @Test
    @TestTransaction
    void saveObjectStorageLinkDatiSpecVersTest() {
	assertDoesNotThrow(() -> dao.saveObjectStorageLinkDatiSpecVers(tenant, bucketName,
		UUID.randomUUID().toString(), 8L,
		new DatiSpecLinkOsKeyMap(258685L, TiEntitaSacerAroVersIniDatiSpecOs.UNI_DOC.name()),
		1L));
    }

    @Test
    @TestTransaction
    void findDatiSpecVersByIdUpdUnitaDocTestNoException() {
	assertDoesNotThrow(() -> dao.findDatiSpecVersByIdVersIniUnitaDoc(258685L));
    }

    @Test
    @TestTransaction
    void deleteBlDatiSpecVersTestNoException() {
	List<Long> list = new ArrayList<Long>();
	list.add(30001L);
	list.add(979361L);
	assertDoesNotThrow(() -> dao.deleteBlDatiSpecVers(list));
    }

    @Test
    @TestTransaction
    void deleteBlDatiSpecVersTestWithException() {
	assertThrows(AppMigrateOsDeleteSrcException.class,
		() -> dao.deleteBlDatiSpecVers(Arrays.asList(Long.MIN_VALUE)));
    }

    @Test
    @TestTransaction
    void deleteBlDatiSpecVersTestWithEmptyList() {
	assertDoesNotThrow(() -> dao.deleteBlDatiSpecVers(new ArrayList<>()));
    }

    @Test
    @TestTransaction
    void findIdsDatiSpecVersAsStreamTestEmptyResult() {
	FilterDto filter = new FilterDto();
	filter.setIdStrut(Long.MIN_VALUE); // non-existent ID to simulate no results

	assertEquals(0L, dao.findIdsDatiSpecVers(filter).count());
    }
}
