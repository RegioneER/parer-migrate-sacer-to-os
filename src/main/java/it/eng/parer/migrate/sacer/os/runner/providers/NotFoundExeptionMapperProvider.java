/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.providers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.exceptions.AppEntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/*
 * ExceptionMapper che gestisce l'http 404 qualora venga richiamata una risorsa non esistente
 * (risposta vuota con il relativo http error code)
 */
@Provider
public class NotFoundExeptionMapperProvider implements ExceptionMapper<AppEntityNotFoundException> {

    private static final Logger log = LoggerFactory.getLogger(NotFoundExeptionMapperProvider.class);

    @Override
    public Response toResponse(AppEntityNotFoundException exception) {
	log.atError().log("Entità non trovata", exception);
	return Response.status(404).entity(Map.of("error", exception.getMessage())).build();
    }

}
