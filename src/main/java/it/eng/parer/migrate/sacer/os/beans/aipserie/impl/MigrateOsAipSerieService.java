package it.eng.parer.migrate.sacer.os.beans.aipserie.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.aipserie.IMigrateOsAipSerieS3Service;
import it.eng.parer.migrate.sacer.os.beans.aipserie.IMigrateOsAipSerieService;
import it.eng.parer.migrate.sacer.os.beans.aipserie.ISacerAipSerieDao;
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
public class MigrateOsAipSerieService extends MigrateOsAbstract
	implements IMigrateOsAipSerieService {

    @Inject
    ISacerAipSerieDao sacerAipDao;

    @Inject
    IMigrateOsAipSerieS3Service osAipS3Service;

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void processMigrationAipSerieFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationAipSerieRequest(List<MigrateRequest> osAipRequests) {
	return super.registerRequestByType(osAipRequests, RequestCnts.Type.AIP_SERIE);
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerAipDao.findIdsVerSerie(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osAipS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.SER_FILE_VER_SERIE_AIP;
    }

}
