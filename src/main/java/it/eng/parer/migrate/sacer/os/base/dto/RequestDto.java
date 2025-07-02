package it.eng.parer.migrate.sacer.os.base.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;

@JsonInclude(Include.NON_NULL)
public class RequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private LocalDateTime dtInsert;
    private LocalDateTime dtStart;
    private LocalDateTime dtFinish;
    private LocalDateTime dtLastUpdate;
    private RequestCnts.State state;
    private Long nrFounded;
    private Long nrDone;
    private String error;
    private Boolean deleteSrc;
    private RequestCnts.Type type;
    private String s3Tenant;
    private String s3BackendName;
    private String hostname;

    private FilterDto filter = null;

    public RequestDto() {
	super();
    }

    private RequestDto(String error, Boolean deleteSrc, RequestCnts.Type type, String s3Tenant,
	    String s3BackendName, FilterDto filter) {
	super();
	this.error = error;
	this.deleteSrc = deleteSrc;
	this.type = type;
	this.s3Tenant = s3Tenant;
	this.s3BackendName = s3BackendName;
	this.filter = filter;
    }

    private RequestDto(String uuid, LocalDateTime dtInsert, LocalDateTime dtStart,
	    LocalDateTime dtFinish, LocalDateTime dtLastUpdate, RequestCnts.State state,
	    Long nrFounded, Long nrDone, String error, Boolean deleteSrc, RequestCnts.Type type,
	    String s3Tenant, String s3BackendName, String hostname, FilterDto filter) {
	super();
	this.uuid = uuid;
	this.dtInsert = dtInsert;
	this.dtStart = dtStart;
	this.dtFinish = dtFinish;
	this.dtLastUpdate = dtLastUpdate;
	this.state = state;
	this.nrFounded = nrFounded;
	this.nrDone = nrDone;
	this.error = error;
	this.deleteSrc = deleteSrc;
	this.type = type;
	this.s3Tenant = s3Tenant;
	this.s3BackendName = s3BackendName;
	this.hostname = hostname;
	this.filter = filter;
    }

    public RequestDto(Requests request) {
	this(request.getUuid(), request.getDtInsert(), request.getDtStart(), request.getDtFinish(),
		request.getDtLastUpdate(), request.getState(), request.getNrObjectFounded(),
		request.getNrObjectMigrated(), request.getErrorDetail(),
		request.getDeleteSourceObj(), request.getMigrationType(), request.getS3Tenant(),
		request.getS3BanckedName(), request.getHostname(),
		new FilterDto(request.getFilter()));
    }

    // empty (case of error)
    public RequestDto(RequestCnts.Type type, Boolean deleteSrc, String s3Tenant,
	    String s3BackendName, FilterDto filter, String error) {
	this(error, deleteSrc, type, s3Tenant, s3BackendName, filter);
    }

    public LocalDateTime getDtInsert() {
	return dtInsert;
    }

    public LocalDateTime getDtStart() {
	return dtStart;
    }

    public LocalDateTime getDtFinish() {
	return dtFinish;
    }

    public LocalDateTime getDtLastUpdate() {
	return dtLastUpdate;
    }

    public String getUuid() {
	return uuid;
    }

    public RequestCnts.State getState() {
	return state;
    }

    public Long getNrFounded() {
	return nrFounded;
    }

    public Long getNrDone() {
	return nrDone;
    }

    public String getError() {
	return error;
    }

    public FilterDto getFilter() {
	return filter;
    }

    public Boolean getDeleteSrc() {
	return deleteSrc;
    }

    public RequestCnts.Type getType() {
	return type;
    }

    public String getS3Tenant() {
	return s3Tenant;
    }

    public String getS3BackendName() {
	return s3BackendName;
    }

    public String getHostname() {
	return hostname;
    }

}