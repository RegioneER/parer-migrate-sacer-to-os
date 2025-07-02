package it.eng.parer.migrate.sacer.os.runner.rest;

import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.RESOURCE_UPDDATISPECINIMIGRATE;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_API_BASE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.base.model.MigrateResponse;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.IMigrateOsUpdDatiSpecAggMdService;
import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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

@Tag(name = "Migrazione Dati Specifici aggiornamento metadati UD", description = "Migrazione dei Dati Specifici dell'aggiornamento metadati UD su Object Storage")
@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@RequestScoped
@Path(URL_API_BASE)
public class MigrateOsUpdDatiSpecAggMdEndpoint {
    /* constants */
    private static final String ETAG = "osupddatispecini-v1.0";

    /* interfaces */
    private final IMigrateOsUpdDatiSpecAggMdService migrateOsUpdDatiSpecAggMdService;

    @Inject
    public MigrateOsUpdDatiSpecAggMdEndpoint(
	    IMigrateOsUpdDatiSpecAggMdService migrateOsUpdDatiSpecAggMdService) {
	this.migrateOsUpdDatiSpecAggMdService = migrateOsUpdDatiSpecAggMdService;
    }

    @Operation(summary = "Creazione richiesta di migrazione con tipologia oggetto: UPD_DATI_SPEC_INI", description = "Effettua migrazione su object storage dei dati specifici dell'aggiornamento metadati ud.")
    @APIResponses(value = {
	    @APIResponse(responseCode = "200", description = "Richiesta di migrazione OS dei dati specifici dell'aggiornamento metadati ud registrata con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MigrateResponse.class))),
	    @APIResponse(responseCode = "400", description = "Richiesta non valida e non registrata", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppBadRequestException.class))),
	    @APIResponse(responseCode = "500", description = "Errore generico (richiesta non valida secondo specifiche)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppGenericRuntimeException.class))) })
    @POST
    @Path(RESOURCE_UPDDATISPECINIMIGRATE)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response migrate(
	    @Valid @NotNull(message = "Necessario indicare un filtro di ricerca") List<MigrateRequest> osUpdDatiSpecAggMdRequests,
	    @Context HttpServerRequest request) {
	// do something .....
	MigrateResponse result = registerOsUpdDatiSpecAggMdRequest(osUpdDatiSpecAggMdRequests,
		request);
	//
	return Response.ok(result)
		.lastModified(
			Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
		.tag(new EntityTag(ETAG)).build();
    }

    private MigrateResponse registerOsUpdDatiSpecAggMdRequest(
	    List<MigrateRequest> osUpdDatiSpecAggMdRequests, HttpServerRequest request) {
	List<RequestDto> result = migrateOsUpdDatiSpecAggMdService
		.registerMigrationUpdDatiSpecAggMdRequest(osUpdDatiSpecAggMdRequests);
	return new MigrateResponse(result, request);
    }
}
