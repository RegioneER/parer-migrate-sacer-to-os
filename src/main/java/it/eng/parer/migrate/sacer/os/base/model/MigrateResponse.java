/**
 *
 */
package it.eng.parer.migrate.sacer.os.base.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.vertx.core.http.HttpServerRequest;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;

@JsonInclude(Include.NON_NULL)
public class MigrateResponse {

    // multi request
    public String moreinfo;
    public Integer founded;
    public List<RequestDto> requests;

    public MigrateResponse() {
	super();
    }

    public MigrateResponse(List<RequestDto> requests, HttpServerRequest request) {
	super();
	this.requests = requests;
	this.founded = requests.size();
	this.moreinfo = request.absoluteURI();
    }

}
