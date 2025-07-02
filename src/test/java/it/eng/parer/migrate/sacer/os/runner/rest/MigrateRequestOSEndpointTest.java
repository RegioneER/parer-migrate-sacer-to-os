package it.eng.parer.migrate.sacer.os.runner.rest;

import static io.restassured.RestAssured.given;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_GET_OS_REQS_REQUEST;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_GET_OS_REQ_REQUEST;
import static org.hamcrest.Matchers.hasKey;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import it.eng.parer.DatabaseInit;
import it.eng.parer.Profiles;
import jakarta.inject.Inject;

@Disabled("problema con H2 in memory")
@QuarkusTest
@TestProfile(Profiles.EndToEnd.class)
class MigrateRequestOSEndpointTest {

    @Inject
    DatabaseInit databaseInit;

    @Test
    @TestTransaction
    @TestSecurity(user = "test_microservizi", roles = {
	    "admin" })
    void successRequests() {
	//
	databaseInit.insertRequest();

	given().when().get(URL_GET_OS_REQS_REQUEST).then().statusCode(200).body("$",
		hasKey("founded"));
    }

    @Test
    @TestTransaction
    @TestSecurity(user = "test_microservizi", roles = {
	    "admin" })
    void successRequest() {
	//
	databaseInit.insertRequest();

	final String uuid = "5b522bd2-efaf-11ef-9b45-0b989b74b7b3";
	given().when().get(URL_GET_OS_REQ_REQUEST + "/" + uuid).then().statusCode(200).body("$",
		hasKey("uuid"));
    }

}
