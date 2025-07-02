/**
 *
 */
package it.eng.parer.migrate.sacer.os.exceptions;

public enum ErrorCategory {

    INTERNAL_ERROR, USER_ERROR, VALIDATION_ERROR, PERSISTENCE, NOT_FOUND_ENTITY, HIBERNATE,
    S3_ERROR;
}