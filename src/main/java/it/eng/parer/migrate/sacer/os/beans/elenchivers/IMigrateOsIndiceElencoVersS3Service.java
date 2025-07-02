package it.eng.parer.migrate.sacer.os.beans.elenchivers;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsIndiceElencoVersS3Service {
    /**
     * Effettua migrazione per singola sessione di versamento
     *
     * @param idFilelencoVers file elenco di versamento
     * @param delete          cancellazione del dato al termine della migrazione
     *
     * @return risorsa migrata su object storage via S3
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    IObjectStorageResource doMigrate(Long idBackend, Long idFileElencoVers, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
