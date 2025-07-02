package it.eng.parer.migrate.sacer.os.beans.base.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateSacerDaoTest {

    static final Long ID_STRUT = 8L; // PARER_TEST

    @Inject
    IMigrateSacerDao dao;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @Test
    @TestTransaction
    void findDecBackendByNameTest() {
	//

	assertDoesNotThrow(() -> dao.findDecBakendByName(nmBackend));
    }

    @Test
    @TestTransaction
    void findDecBackendByNameKoTest() {
	//
	final String backendNameNE = "prova";
	assertThrows(Exception.class, () -> dao.findDecBakendByName(backendNameNE),
		"Backend name not valid");
    }

    @Test
    @TestTransaction
    void findNmEnteAndNmStrutByIdStrutTest() {
	//
	int idx = 0;
	Object[] result = assertDoesNotThrow(() -> dao.findNmEnteAndNmStrutByIdStrut(ID_STRUT));
	assertEquals("ente_test", result[idx]);
	assertEquals("PARER_TEST", result[++idx]);
    }

    @Test
    @TestTransaction
    void findNmEnteAndNmStrutByIdStrutKoTest() {
	//
	assertThrows(Exception.class, () -> dao.findNmEnteAndNmStrutByIdStrut(Long.MIN_VALUE),
		"Struttura not valid");
    }

}