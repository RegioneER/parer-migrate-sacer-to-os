package it.eng.parer.migrate.sacer.os.beans.aipserie;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsAipSerieS3Service {
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idFileVerSerie, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
