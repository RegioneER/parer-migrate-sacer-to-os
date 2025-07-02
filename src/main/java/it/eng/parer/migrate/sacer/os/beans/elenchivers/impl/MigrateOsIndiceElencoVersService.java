package it.eng.parer.migrate.sacer.os.beans.elenchivers.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.IMigrateOsIndiceElencoVersS3Service;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.IMigrateOsIndiceElencoVersService;
import it.eng.parer.migrate.sacer.os.beans.elenchivers.ISacerElencoVersDao;
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
public class MigrateOsIndiceElencoVersService extends MigrateOsAbstract
	implements IMigrateOsIndiceElencoVersService {

    @Inject
    ISacerElencoVersDao sacerElencoVersDao;

    @Inject
    IMigrateOsIndiceElencoVersS3Service osElencoVersS3Service;

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void processMigrationIndiceElencoVersUdFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationIndiceElencoVersUdRequest(
	    List<MigrateRequest> osElencoVersRequests) {
	return super.registerRequestByType(osElencoVersRequests,
		RequestCnts.Type.INDICE_ELENCO_VERS);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osElencoVersS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.INDICE_ELENCO_VERS;
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerElencoVersDao.findIdsElvElencoVers(filter);
    }
}
