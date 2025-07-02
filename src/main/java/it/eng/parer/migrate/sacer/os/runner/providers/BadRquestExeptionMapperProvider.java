/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.providers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/*
 * ExceptionMapper che gestisce l'http 404 qualora venga richiamata una risorsa non esistente
 * (risposta vuota con il relativo http error code)
 */
@Provider
public class BadRquestExeptionMapperProvider implements ExceptionMapper<AppBadRequestException> {

    private static final Logger log = LoggerFactory
	    .getLogger(BadRquestExeptionMapperProvider.class);

    @Override
    public Response toResponse(AppBadRequestException exception) {
	log.atError().log("Richiesta errata", exception);
	return Response.status(400).entity(Map.of("error", exception.getMessage())).build();
    }

}
