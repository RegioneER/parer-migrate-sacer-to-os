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
