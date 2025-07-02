/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.exceptions;

import java.text.MessageFormat;

public class AppMigrateOsDeleteSrcException extends Exception {

    private static final long serialVersionUID = 5015771412184277789L;

    private final ErrorCategory category;

    public AppMigrateOsDeleteSrcException(AppMigrateOsSipS3ExceptionBuilder builder) {
	super(builder.message, builder.cause);
	this.category = builder.category;
    }

    public ErrorCategory getCategory() {
	return category;
    }

    public static AppMigrateOsSipS3ExceptionBuilder builder() {
	return new AppMigrateOsSipS3ExceptionBuilder();
    }

    @Override
    public String getMessage() {
	return "[" + getCategory().toString() + "]" + "  " + super.getMessage();
    }

    public static class AppMigrateOsSipS3ExceptionBuilder {

	private ErrorCategory category = ErrorCategory.PERSISTENCE; // default
	private String message;
	private Throwable cause;

	public AppMigrateOsDeleteSrcException build() {
	    return new AppMigrateOsDeleteSrcException(this);
	}

	public ErrorCategory getCategory() {
	    return category;
	}

	public AppMigrateOsSipS3ExceptionBuilder message(String messageToFormat, Object... args) {
	    this.setMessage(MessageFormat.format(messageToFormat, args));
	    return this;
	}

	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}

	public AppMigrateOsSipS3ExceptionBuilder cause(Throwable cause) {
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
