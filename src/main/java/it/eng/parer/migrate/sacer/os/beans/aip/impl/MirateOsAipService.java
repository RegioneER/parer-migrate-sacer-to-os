package it.eng.parer.migrate.sacer.os.beans.aip.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.aip.IMigrateOsAipS3Service;
import it.eng.parer.migrate.sacer.os.beans.aip.IMigrateOsAipService;
import it.eng.parer.migrate.sacer.os.beans.aip.ISacerAipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MirateOsAipService extends MigrateOsAbstract implements IMigrateOsAipService {
    @Inject
    ISacerAipDao sacerAipDao;

    @Inject
    IMigrateOsAipS3Service osAipS3Service;

    @Override
    public void processMigrationAipFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationAipRequest(List<MigrateRequest> osAipRequests) {
	return super.registerRequestByType(osAipRequests, RequestCnts.Type.AIP);
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerAipDao.findIdsVerAip(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osAipS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ARO_INDICE_AIP_UD;
    }
}
