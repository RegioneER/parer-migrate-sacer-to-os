package it.eng.parer.migrate.sacer.os.base;

/**
 * Informazioni per la risorsa salvata sull'object storage.
 *
 */
public interface IObjectStorageResource {

    String getS3Bucket();

    String getS3Key();

    String getS3Checksum();

    String getETag();

    String getObjBase64();

    String getSHA256();

    String getTenant();

}
