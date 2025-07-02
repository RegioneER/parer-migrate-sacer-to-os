package it.eng.parer.migrate.sacer.os.beans.comp;

import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroCompDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;

public interface ISacerCompDao {

    Stream<Long> findIdsCompDoc(FilterDto filter);

    Stream<Long> findIdsCompDocOnView(FilterDto filter);

    void saveObjectStorageLinkComp(String tenant, String bucket, String key, Long idCompDoc,
	    Long idBackend);

    AroCompDoc findCompDocById(Long idCompDoc) throws AppMigrateOsS3Exception;

    void deleteBlContenutoComp(Long idContenutoComp) throws AppMigrateOsDeleteSrcException;

    Long getIdUnitaDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception;

    Long getIdDocByIdCompDoc(Long idCompDoc) throws AppMigrateOsS3Exception;

    VrsSessioneVers findVrsSessioneVersByIdStrutAndIdCompDoc(Long idStrut, Long idCompDoc)
	    throws AppMigrateOsS3Exception;
}
