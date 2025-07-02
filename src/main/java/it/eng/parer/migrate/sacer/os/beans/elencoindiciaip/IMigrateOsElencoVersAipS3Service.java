package it.eng.parer.migrate.sacer.os.beans.elencoindiciaip;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsElencoVersAipS3Service {
    /**
     * Effettua migrazione per singola sessione di versamento
     *
     * @param idElencoVers elenco di versamento
     * @param delete       cancellazione del dato al termine della migrazione
     *
     * @return risorsa migrata su object storage via S3
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idElencoVers, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
