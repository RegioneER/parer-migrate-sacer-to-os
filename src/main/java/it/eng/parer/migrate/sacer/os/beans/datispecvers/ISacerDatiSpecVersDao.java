package it.eng.parer.migrate.sacer.os.beans.datispecvers;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniDatiSpec;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroVersIniUnitaDoc;

public interface ISacerDatiSpecVersDao {
    Stream<Long> findIdsDatiSpecVers(FilterDto filter);

    void saveObjectStorageLinkDatiSpecVers(String tenant, String bucket, String key, Long idStrut,
	    DatiSpecLinkOsKeyMap datiSpec, Long idBackend);

    List<AroVersIniDatiSpec> findDatiSpecVersByIdVersIniUnitaDoc(Long idVersIniUnitaDoc)
	    throws AppMigrateOsS3Exception;

    void deleteBlDatiSpecVers(List<Long> versIniDatiSpecIds) throws AppMigrateOsDeleteSrcException;

    public AroVersIniUnitaDoc findAroVersIniUnitaDocById(Long idVersIniUnitaDoc)
	    throws AppMigrateOsS3Exception;
}
