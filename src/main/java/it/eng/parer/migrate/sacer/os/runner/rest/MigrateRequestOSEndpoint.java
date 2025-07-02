package it.eng.parer.migrate.sacer.os.runner.rest;

import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.RESOURCE_REQUEST;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.RESOURCE_REQUESTS;
import static it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.URL_API_BASE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsService;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateResponse;
import it.eng.parer.migrate.sacer.os.exceptions.AppEntityNotFoundException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByCol;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByTo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Tag(name = "Richieste migrazione os", description = "Gestione delle richiesta di Migrazione su Object Storage")
@SecurityScheme(securitySchemeName = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@RequestScoped
@Path(URL_API_BASE)
public class MigrateRequestOSEndpoint {

    /* constants */
    private static final String ETAG = "osreq-v1.0";

    @ConfigProperty(name = "quarkus.uuid")
    String instanceUUID;

    /* interfaces */
    private final IMigrateOsService migrateOsService;

    @Inject
    public MigrateRequestOSEndpoint(IMigrateOsService migrateOsService) {
	this.migrateOsService = migrateOsService;
    }

    @Operation(summary = "Lista richieste di migrazione OS applicando con filtro", description = "Lista richieste di migrazione OS SIP applicando con filtro")
    @APIResponses(value = {
	    @APIResponse(responseCode = "200", description = "Lista recuperata con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MigrateResponse.class))),
	    @APIResponse(responseCode = "500", description = "Errore generico", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppGenericRuntimeException.class))) })
    @GET
    @Path(RESOURCE_REQUESTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response requestCnts(
	    @Parameter(allowEmptyValue = true, schema = @Schema(type = SchemaType.STRING, required = false, implementation = RequestCnts.State.class)) @QueryParam(value = "state") String state,
	    @Parameter(allowEmptyValue = true, schema = @Schema(type = SchemaType.STRING, required = false, implementation = RequestCnts.Type.class)) @QueryParam(value = "type") String type,
	    @Parameter(allowEmptyValue = true, schema = @Schema(type = SchemaType.STRING, required = false, format = "YYYY-mm-dd", implementation = LocalDate.class)) @QueryParam(value = "dtstart") LocalDate dtstart,
	    @Parameter(allowEmptyValue = true, schema = @Schema(type = SchemaType.STRING, required = false, format = "YYYY-mm-dd", implementation = LocalDate.class)) @QueryParam(value = "dtfinish") LocalDate dtfinish,
	    @Parameter(allowEmptyValue = false, schema = @Schema(type = SchemaType.STRING, required = true, implementation = OsRequestOrderByCol.class)) @DefaultValue(value = "DT_INSERT") @QueryParam(value = "orderbycol") String orderbycol,
	    @Parameter(allowEmptyValue = false, schema = @Schema(type = SchemaType.STRING, required = true, implementation = OsRequestOrderByTo.class)) @DefaultValue(value = "ASC") @QueryParam(value = "orderbyto") String orderbyto,
	    @Parameter(allowEmptyValue = true, schema = @Schema(type = SchemaType.INTEGER, required = false, implementation = Integer.class)) @DefaultValue(value = "100") @QueryParam(value = "maxresult") Integer maxresult,
	    @Context HttpServerRequest request) {
	// do something .....
	MigrateResponse results = listOfOsSipRequests(state, type, dtstart, dtfinish, request,
		orderbycol, orderbyto, maxresult);
	//
	return Response.ok(results)
		.lastModified(
			Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
		.tag(new EntityTag(ETAG)).build();
    }

    @Operation(summary = "Verifica stato richiesta migrazione OS", description = "Verifica stato richiesta migrazione OS SIP")
    @APIResponses(value = {
	    @APIResponse(responseCode = "200", description = "Richiesta recuperata con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MigrateResponse.class))),
	    @APIResponse(responseCode = "404", description = "Richiesta non esistente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppEntityNotFoundException.class))),
	    @APIResponse(responseCode = "500", description = "Errore generico", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppGenericRuntimeException.class))) })
    @GET
    @Path(RESOURCE_REQUEST + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response requestByUuid(@PathParam(value = "uuid") String uuid,
	    @Context HttpServerRequest request) {

	// do something .....
	MigrateResponse result = getOsRequest(uuid, request);
	//
	return Response.ok(result)
		.lastModified(Date.from(result.requests.get(0).getDtLastUpdate()
			.atZone(ZoneId.systemDefault()).toInstant()))
		.tag(new EntityTag(ETAG)).build();
    }

    private MigrateResponse getOsRequest(String uuid, HttpServerRequest request) {
	RequestDto result = migrateOsService.findOsRequestByUuid(uuid);
	return new MigrateResponse(Arrays.asList(result), request);
    }

    private MigrateResponse listOfOsSipRequests(String state, String type, LocalDate dtstart,
	    LocalDate dtfinish, HttpServerRequest request, String orderby, String orderbyto,
	    Integer maxresult) {
	//
	List<RequestDto> results = migrateOsService.findOsRequests(state, type, dtstart, dtfinish,
		orderby, orderbyto, maxresult);
	return new MigrateResponse(results, request);
    }
}
