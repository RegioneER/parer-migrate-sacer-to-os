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
