package it.eng.parer.migrate.sacer.os.beans.datispecvers;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsDatiSpecVersS3Service {
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idVersIniUnitaDoc, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
