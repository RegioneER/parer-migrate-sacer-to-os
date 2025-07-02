package it.eng.parer.migrate.sacer.os.beans.upddatispec;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsUpdDatiSpecAggMdS3Service {
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idUpdUnitaDoc, Boolean delete)
	    throws AppMigrateOsS3Exception;
}
