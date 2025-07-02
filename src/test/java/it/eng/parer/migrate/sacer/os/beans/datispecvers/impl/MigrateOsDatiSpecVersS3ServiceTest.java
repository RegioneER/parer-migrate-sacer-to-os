package it.eng.parer.migrate.sacer.os.beans.datispecvers.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.IMigrateOsDatiSpecVersS3Service;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsDatiSpecVersS3ServiceTest {
    @Inject
    IMigrateOsDatiSpecVersS3Service service;

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    @Disabled("Disabled for testing purposes")
    void doMigrate_ok() {
	assertDoesNotThrow(() -> service.doMigrate(1L, 47471L, Boolean.FALSE));
    }

    @Test
    @TestTransaction
    void doMigrate_nullInputs() {
	assertThrows(AppGenericRuntimeException.class, () -> service.doMigrate(null, null, null));
    }

}
