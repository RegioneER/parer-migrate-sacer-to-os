package it.eng.parer.migrate.sacer.os.beans.aip;

import java.math.BigDecimal;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface ISacerAipDao {

    Stream<Long> findIdsVerAip(FilterDto filter);

    void saveObjectStorageLinkAip(String tenant, String bucket, String key, Long idSubStrut,
	    BigDecimal aaKeyUnitaDoc, Long idVerIndiceAip, Long idBackend);

    Object[] findIndiceAndFileAipWithUdAndVrsSessByIdVerIndiceAip(Long idVerIndiceAip)
	    throws AppMigrateOsS3Exception;

    void deleteBlFileVerIndiceAip(Long idFileVerIndiceAIp) throws AppMigrateOsDeleteSrcException;

}
