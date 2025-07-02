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

package it.eng.parer.migrate.sacer.os.beans.job.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import it.eng.parer.migrate.sacer.os.beans.job.IMigrateOsJobDao;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(Profiles.H2.class)
class MigrateOsJobDaoTest {

    @Inject
    IMigrateOsJobDao dao;

    @Inject
    DatabaseInit databaseInit;

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Test
    @TestTransaction
    void trueExceedNrRequestRunningPerHostTest() {
	//
	databaseInit.insertRequest();
	assertEquals(true, dao.exceedNrRequestRunningPerHost(1, "test-host"));
    }

    @Test
    @TestTransaction
    void falseExceedNrRequestRunningPerHostTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.exceedNrRequestRunningPerHost(2, "test-host"));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnSipTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.SIP));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnCompTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.COMP));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnAipTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.AIP));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnAipSerieTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.AIP_SERIE));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnElencoIndiciAipTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false,
		dao.notExitsRequestRegisteredByType(RequestCnts.Type.ELENCO_INDICI_AIP));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnSipUpdTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.SIPUPDUD));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnIndiceElencoVersTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false,
		dao.notExitsRequestRegisteredByType(RequestCnts.Type.INDICE_ELENCO_VERS));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnUpdDatiSpecIniTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false,
		dao.notExitsRequestRegisteredByType(RequestCnts.Type.UPD_DATI_SPEC_INI));
    }

    @Test
    @TestTransaction
    void trueExistsRequestRegisteredOnDatiSpecVersTest() {
	//
	databaseInit.insertRequest();
	assertEquals(false, dao.notExitsRequestRegisteredByType(RequestCnts.Type.DATI_SPEC_VERS));
    }
}
