/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.exceptions;

import java.text.MessageFormat;
import java.util.Objects;

import jakarta.ws.rs.WebApplicationException;

public class AppGenericRuntimeException extends WebApplicationException {

    private static final long serialVersionUID = 5015771412184277789L;

    private final ErrorCategory category;

    private final String uuidMigrateReq;

    public AppGenericRuntimeException(AppGenericRuntimeExceptionBuilder builder) {
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

    public static AppGenericRuntimeExceptionBuilder builder() {
	return new AppGenericRuntimeExceptionBuilder();
    }

    @Override
    public String getMessage() {
	return "[" + getCategory().toString() + "]"
		+ (Objects.nonNull(uuidMigrateReq) ? " - (UUID REQ: " + getUuidMigrateReq() + ")"
			: "")
		+ " " + super.getMessage();
    }

    public static class AppGenericRuntimeExceptionBuilder {

	private ErrorCategory category = ErrorCategory.INTERNAL_ERROR; // default
	private String message;
	private Throwable cause;
	private String uuidMigrateReq;

	public AppGenericRuntimeException build() {
	    return new AppGenericRuntimeException(this);
	}

	public AppGenericRuntimeExceptionBuilder category(ErrorCategory category) {
	    this.setCategory(category);
	    return this;
	}

	public ErrorCategory getCategory() {
	    return category;
	}

	public void setCategory(ErrorCategory category) {
	    this.category = category;
	}

	public AppGenericRuntimeExceptionBuilder message(String messageToFormat, Object... args) {
	    this.setMessage(MessageFormat.format(messageToFormat, args));
	    return this;
	}

	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}

	public AppGenericRuntimeExceptionBuilder cause(Throwable cause) {
	    this.setCause(cause);
	    return this;
	}

	public Throwable getCause() {
	    return cause;
	}

	public void setCause(Throwable cause) {
	    this.cause = cause;
	}

	public AppGenericRuntimeExceptionBuilder uuidMigrateReq(String uuidMigrateReq) {
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
