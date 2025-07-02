package it.eng.parer.migrate.sacer.os.runner.rest;

import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.RESOURCE_UPDUDMIGRATE;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_API_BASE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.base.model.MigrateResponse;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.IMigrateOsSipAggMetadatiService;
import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Tag(name = "Migrazione SIP UPD", description = "Migrazione sip aggiornamento metadati dell'unità documentaria su Object Storage")
@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@RequestScoped
@Path(URL_API_BASE)
public class MigrateOSSipUpdUdEndpoint {

    /* constants */
    private static final String ETAG = "ossip-v1.0";

    /* interfaces */
    private final IMigrateOsSipAggMetadatiService migrateOsSipAggMetadatiService;

    @Inject
    public MigrateOSSipUpdUdEndpoint(
	    IMigrateOsSipAggMetadatiService migrateOsSipAggMetadatiService) {
	this.migrateOsSipAggMetadatiService = migrateOsSipAggMetadatiService;
    }

    @Operation(summary = "Creazione richiesta di migrazione con tipologia oggetto: SIPUPDUD", description = "Effettua migrazione su object storage dei SIP di aggiornamento metadati di unità documentarie")
    @APIResponses(value = {
	    @APIResponse(responseCode = "200", description = "Richiesta di migrazione OS SIP registrata con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MigrateResponse.class))),
	    @APIResponse(responseCode = "400", description = "Richiesta non valida e non registrata", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppBadRequestException.class))),
	    @APIResponse(responseCode = "500", description = "Errore generico (richiesta non valida secondo specifiche)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppGenericRuntimeException.class))) })
    @POST
    @Path(RESOURCE_UPDUDMIGRATE)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response migrate(
	    @Valid @NotNull(message = "Necessario indicare un filtro di ricerca") List<MigrateRequest> osSipRequests,
	    @Context HttpServerRequest request) {
	// do something .....
	MigrateResponse result = registerOsSipRequest(osSipRequests, request);
	//
	return Response.ok(result)
		.lastModified(
			Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
		.tag(new EntityTag(ETAG)).build();
    }

    private MigrateResponse registerOsSipRequest(List<MigrateRequest> osSipRequests,
	    HttpServerRequest request) {
	List<RequestDto> result = migrateOsSipAggMetadatiService
		.registerMigrationSipRequest(osSipRequests);
	return new MigrateResponse(result, request);
    }

}
