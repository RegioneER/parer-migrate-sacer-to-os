package it.eng.parer.migrate.sacer.os.beans.elenchivers.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.IMigrateOsIndiceElencoVersS3Service;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsIndiceElencoVersS3ServiceTest {
    @Inject
    IMigrateOsIndiceElencoVersS3Service service;

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    @Disabled("Disabled for testing purposes")
    void registerRequest_ok() {
	assertDoesNotThrow(() -> service.doMigrate(1L, 396835216L, Boolean.FALSE));
    }

    @Test
    @TestTransaction
    void doMigrate_nullInputs() {
	assertThrows(AppMigrateOsS3Exception.class, () -> service.doMigrate(null, null, null));
    }

}
