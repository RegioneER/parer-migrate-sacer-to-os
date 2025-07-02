package it.eng.parer.migrate.sacer.os.beans.comp;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsCompS3Service {
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idCompDoc, Boolean delete)
	    throws AppMigrateOsS3Exception;

}
