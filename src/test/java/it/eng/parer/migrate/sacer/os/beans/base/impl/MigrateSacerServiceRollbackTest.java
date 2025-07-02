package it.eng.parer.migrate.sacer.os.beans.base.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.base.IMigrateSacerService;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateSacerServiceRollbackTest {

    @InjectMock
    IMigrateSacerService serviceMock;

    @Inject
    IMigrateSacerService service;

    @Test
    @TestTransaction
    void getIdDecBackEnd_ko() {

	when(serviceMock.getIdDeckBackeEnd()).thenThrow(appGenericRuntimeException());

        final AppGenericRuntimeException appException = assertThrows(AppGenericRuntimeException.class,
                () -> service.getIdDeckBackeEnd(), "Should fail throwing AppGenericRuntimeException");
        assertEquals(ErrorCategory.INTERNAL_ERROR, appException.getCategory());
    }

    private AppGenericRuntimeException appGenericRuntimeException() {
	return AppGenericRuntimeException.builder().category(ErrorCategory.INTERNAL_ERROR)
		.message("Errore generico").build();
    }
}