/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.exceptions;

import java.text.MessageFormat;
import java.util.Objects;

import jakarta.ws.rs.WebApplicationException;

public class AppEntityNotFoundException extends WebApplicationException {

    private static final long serialVersionUID = -7917206270508553978L;

    private final ErrorCategory category;

    private final String uuidMigrateReq;

    public AppEntityNotFoundException(AppEntityNotFoundExceptionBuilder builder) {
	super(builder.message, builder.cause);
	this.category = builder.category;
	this.uuidMigrateReq = builder.uuidMigrateReq;
    }

    public ErrorCategory getCategory() {
	return category;
    }

    public String getUuidMigrateReq() {
	return uuidMigrateReq;
    }

    public static AppEntityNotFoundExceptionBuilder builder() {
	return new AppEntityNotFoundExceptionBuilder();
    }

    @Override
    public String getMessage() {
	return "[" + getCategory().toString() + "]"
		+ (Objects.nonNull(uuidMigrateReq) ? " - (UUID REQ: " + getUuidMigrateReq() + ")"
			: "")
		+ " " + super.getMessage();
    }

    public static class AppEntityNotFoundExceptionBuilder {

	private ErrorCategory category = ErrorCategory.NOT_FOUND_ENTITY;
	private String message;
	private Throwable cause;
	private String uuidMigrateReq;

	public AppEntityNotFoundException build() {
	    return new AppEntityNotFoundException(this);
	}

	public AppEntityNotFoundExceptionBuilder category(ErrorCategory category) {
	    this.setCategory(category);
	    return this;
	}

	public ErrorCategory getCategory() {
	    return category;
	}

	public void setCategory(ErrorCategory category) {
	    this.category = category;
	}

	public AppEntityNotFoundExceptionBuilder message(String messageToFormat, Object... args) {
	    this.setMessage(MessageFormat.format(messageToFormat, args));
	    return this;
	}

	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}

	public AppEntityNotFoundExceptionBuilder cause(Throwable cause) {
	    this.setCause(cause);
	    return this;
	}

	public Throwable getCause() {
	    return cause;
	}

	public void setCause(Throwable cause) {
	    this.cause = cause;
	}

	public AppEntityNotFoundExceptionBuilder uuidMigrateReq(String uuidMigrateReq) {
	    this.setUuidMigrateReq(uuidMigrateReq);
	    return this;
	}

	public String getUuidMigrateReq() {
	    return uuidMigrateReq;
	}

	public void setUuidMigrateReq(String uuidMigrateReq) {
	    this.uuidMigrateReq = uuidMigrateReq;
	}

    }
}
