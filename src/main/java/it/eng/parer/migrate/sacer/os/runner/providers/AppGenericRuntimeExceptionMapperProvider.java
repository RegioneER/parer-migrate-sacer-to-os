/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/*
 * ExceptionMapper che gestisce gli errori di tipoo WebApplicationException, nel caso specifico si
 * deve verificare la logica del provider CustomJaxbMessageBodyReader il quale, come da default,
 * utilizza questo tipo di eccezioni.
 */
@Provider
public class AppGenericRuntimeExceptionMapperProvider
	implements ExceptionMapper<AppGenericRuntimeException> {

    private static final Logger log = LoggerFactory
	    .getLogger(AppGenericRuntimeExceptionMapperProvider.class);

    @Override
    public Response toResponse(AppGenericRuntimeException exception) {
	log.atError().log("Eccezione generica", exception);
	return Response.status(500).entity(exception).build();
    }

}
