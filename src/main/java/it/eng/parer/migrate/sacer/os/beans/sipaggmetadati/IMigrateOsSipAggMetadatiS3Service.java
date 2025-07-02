package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsSipAggMetadatiS3Service {

    IObjectStorageResource doMigrate(Long idSacerBackend, Long idSipUpdUd, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
