package it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.IMigrateOsElencoVersAipS3Service;
import it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.IMigrateOsElencoVersAipService;
import it.eng.parer.migrate.sacer.os.beans.elencoindiciaip.ISacerElencoVersAipDao;
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
public class MigrateOsElencoVersAipService extends MigrateOsAbstract
	implements IMigrateOsElencoVersAipService {

    @Inject
    ISacerElencoVersAipDao sacerElencoVersAipDao;

    @Inject
    IMigrateOsElencoVersAipS3Service osElencoVersAipS3Service;

    @Override
    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void processMigrationIndiceAipElencoVersUdFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationIndiceAipElencoVersUdRequest(
	    List<MigrateRequest> osElencoVersAipRequests) {
	return super.registerRequestByType(osElencoVersAipRequests,
		RequestCnts.Type.ELENCO_INDICI_AIP);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ELENCO_INDICI_AIP;
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerElencoVersAipDao.findIdsElvElencoVers(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osElencoVersAipS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

}
