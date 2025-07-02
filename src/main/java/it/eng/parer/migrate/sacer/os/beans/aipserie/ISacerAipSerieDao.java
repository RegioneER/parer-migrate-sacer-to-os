package it.eng.parer.migrate.sacer.os.beans.aipserie;

import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.SerFileVerSerieCnts.TiFileVerSerie;

public interface ISacerAipSerieDao {

    Stream<Long> findIdsVerSerie(FilterDto filter);

    void saveObjectStorageLinkAipSerie(String tenant, String bucket, String key, Long idStrut,
	    Long idVerSerie, TiFileVerSerie tiFileVerSerie, Long idBackend);

    Object[] findSerSerieAndIndiceAndFileAipByIdFileVerSerie(Long idFileVerSerie)
	    throws AppMigrateOsS3Exception;

    void deleteBlFileVerSerie(Long idFileVerSerie) throws AppMigrateOsDeleteSrcException;

}
