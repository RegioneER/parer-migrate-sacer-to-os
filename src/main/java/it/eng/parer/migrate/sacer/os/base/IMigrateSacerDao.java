package it.eng.parer.migrate.sacer.os.base;

import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.DecBackend;

public interface IMigrateSacerDao {

    DecBackend findDecBakendByName(String backedName);

    Object[] findNmEnteAndNmStrutByIdStrut(Long idStrut) throws AppMigrateOsS3Exception;

}
