package it.eng.parer.migrate.sacer.os.beans.upddatispec;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.DatiSpecLinkOsKeyMap;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.AroUpdDatiSpecUnitaDoc;

public interface ISacerUpdDatiSpecAggMdDao {
    Stream<Long> findIdsUpdDatiSpecAggMd(FilterDto filter);

    void saveObjectStorageLinkUpdDatiSpecAggMd(String tenant, String bucket, String key,
	    Long idStrut, DatiSpecLinkOsKeyMap datiSpec, Long idBackend);

    List<AroUpdDatiSpecUnitaDoc> findUpdDatiSpecAggMdByIdUpdUnitaDoc(Long idUpdDatiSpecUd)
	    throws AppMigrateOsS3Exception;

    void deleteBlUpdDatiSpecAggMd(List<Long> udpDatiSpecUnitaDocIds)
	    throws AppMigrateOsDeleteSrcException;
}
