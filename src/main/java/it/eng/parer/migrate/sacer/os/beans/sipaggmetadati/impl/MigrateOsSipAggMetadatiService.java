package it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.IMigrateOsSipAggMetadatiS3Service;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.IMigrateOsSipAggMetadatiService;
import it.eng.parer.migrate.sacer.os.beans.sipaggmetadati.ISacerSipAggMetadatiDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsSipAggMetadatiService extends MigrateOsAbstract
	implements IMigrateOsSipAggMetadatiService {

    @Inject
    ISacerSipAggMetadatiDao sacerSipDao;

    @Inject
    IMigrateOsSipAggMetadatiS3Service osSipS3Service;

    @Override
    public List<RequestDto> registerMigrationSipRequest(List<MigrateRequest> osSipRequests) {
	return super.registerRequestByType(osSipRequests, RequestCnts.Type.SIPUPDUD);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ARO_UPD_UNITA_DOC;
    }

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void processMigrationSipFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerSipDao.findIdsUpdUnitaDoc(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osSipS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

}
