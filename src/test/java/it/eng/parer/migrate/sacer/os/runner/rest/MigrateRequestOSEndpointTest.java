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
