package it.eng.parer.migrate.sacer.os.beans.base.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateSacerService;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateSacerServiceTest {

    @Inject
    IMigrateSacerService service;

    @Test
    @TestTransaction
    void getIdDecBackEnd_ok() {

	Long id = assertDoesNotThrow(() -> service.getIdDeckBackeEnd());
	assertNotNull(id);
    }

}