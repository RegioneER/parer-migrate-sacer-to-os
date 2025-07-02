/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.qute;

import org.eclipse.microprofile.openapi.annotations.Operation;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import it.eng.parer.migrate.sacer.os.runner.qute.model.ApplicationInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("welcome")
public class Welcome {

    @Inject
    Template welcome;

    @Inject
    ApplicationInfo appinfo;

    @Operation(hidden = true)
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
	return welcome.data("appinfo", appinfo);
    }

}
