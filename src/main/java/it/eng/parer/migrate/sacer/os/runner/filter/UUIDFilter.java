/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.filter;

import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.MDC_LOG_UUID;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.slf4j.MDC;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;

/*
 * Filtro che intercetta le chiamate di tipo POST e inietta sull'MCD un UUID
 */
public class UUIDFilter {

    @ConfigProperty(name = "quarkus.uuid")
    String instanceUUID;

    @ServerRequestFilter
    public Optional<RestResponse<Void>> getFilter(ContainerRequestContext ctx) {

	if (ctx.getMethod().equals(HttpMethod.POST)) {
	    MDC.put(MDC_LOG_UUID, instanceUUID);
	}

	return Optional.empty();
    }
}
