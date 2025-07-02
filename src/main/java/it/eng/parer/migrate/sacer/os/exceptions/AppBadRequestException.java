/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.exceptions;

import java.text.MessageFormat;

import jakarta.ws.rs.WebApplicationException;

public class AppBadRequestException extends WebApplicationException {

    private static final long serialVersionUID = -7917206270508553978L;

    private final ErrorCategory category;

    public AppBadRequestException(AppBadRequestExceptionBuilder builder) {
	super(builder.message, builder.cause);
	this.category = builder.category;
    }

    public ErrorCategory getCategory() {
	return category;
    }

    public static AppBadRequestExceptionBuilder builder() {
	return new AppBadRequestExceptionBuilder();
    }

    @Override
    public String getMessage() {
	return "[" + getCategory().toString() + "]" + "  " + super.getMessage();
    }

    public static class AppBadRequestExceptionBuilder {

	private ErrorCategory category = ErrorCategory.VALIDATION_ERROR;
	private String message;
	private Throwable cause;

	public AppBadRequestException build() {
	    return new AppBadRequestException(this);
	}

	public AppBadRequestExceptionBuilder category(ErrorCategory category) {
	    this.setCategory(category);
	    return this;
	}

	public ErrorCategory getCategory() {
	    return category;
	}

	public void setCategory(ErrorCategory category) {
	    this.category = category;
	}

	public AppBadRequestExceptionBuilder message(String messageToFormat, Object... args) {
	    this.setMessage(MessageFormat.format(messageToFormat, args));
	    return this;
	}

	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}

	public AppBadRequestExceptionBuilder cause(Throwable cause) {
	    this.setCause(cause);
	    return this;
	}

	public Throwable getCause() {
	    return cause;
	}

	public void setCause(Throwable cause) {
	    this.cause = cause;
	}

    }

}
