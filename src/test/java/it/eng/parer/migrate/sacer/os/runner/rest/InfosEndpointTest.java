package it.eng.parer.migrate.sacer.os.runner.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_GET_INFOS;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import it.eng.parer.Profiles;

@QuarkusTest
@TestProfile(Profiles.EndToEnd.class)
class InfosEndpointTest {

    @Test
    @TestSecurity(user = "test_microservizi", roles = {
	    "admin" })
    void successAny() {
	given().when().get(URL_GET_INFOS).then().statusCode(200).body("$", hasKey("git"))
		.body("git.git.build.version", not(empty()));
    }

    @Test
    @TestSecurity(authorizationEnabled = true)
    void authRequest() {
	given().when().get(URL_GET_INFOS).then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "fakeuser", roles = {
	    "not_admin" })
    void notAuthRequest() {
	given().when().get(URL_GET_INFOS).then().statusCode(403);
    }
}
