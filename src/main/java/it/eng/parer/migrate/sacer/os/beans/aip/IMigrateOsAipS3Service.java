package it.eng.parer.migrate.sacer.os.beans.aip;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsAipS3Service {
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idVerIndiceAip, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
