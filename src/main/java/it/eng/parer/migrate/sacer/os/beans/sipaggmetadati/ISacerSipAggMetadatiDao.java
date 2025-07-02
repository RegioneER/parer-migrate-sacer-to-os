package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroXmlUpdUnitaDoc;

public interface ISacerSipAggMetadatiDao {
    /**
     *
     * @param filter filtro applicato
     *
     * @return lista pk
     */
    Stream<Long> findIdsUpdUnitaDoc(FilterDto filter);

    AroUpdUnitaDoc findAroUpdUnitaDocById(Long idUpdUnitaDoc) throws AppMigrateOsS3Exception;

    void deleteBlXmlOnAroXmlUpdUnitaDoc(List<Long> xmlUpdUnitaDocIds)
	    throws AppMigrateOsDeleteSrcException;

    void saveObjectStorageLinkSipUdAggMd(String tenant, String bucket, String key,
	    long idUpdUnitaDoc, long idStrut, Long idSacerBackend);

    List<AroXmlUpdUnitaDoc> findAllXmlUpdUnitaDocByIdUpdUnitaDoc(Long idUpdUnitaDoc)
	    throws AppMigrateOsS3Exception;

}
