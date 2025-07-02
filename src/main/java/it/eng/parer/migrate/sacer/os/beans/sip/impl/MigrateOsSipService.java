/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.beans.sip.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.sip.IMigrateOsSipS3Service;
import it.eng.parer.migrate.sacer.os.beans.sip.IMigrateOsSipService;
import it.eng.parer.migrate.sacer.os.beans.sip.ISacerSipDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsSipService extends MigrateOsAbstract implements IMigrateOsSipService {

    @Inject
    ISacerSipDao sacerSipDao;

    @Inject
    IMigrateOsSipS3Service osSipS3Service;

    @Override
    public void processMigrationSipFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationSipRequest(List<MigrateRequest> osRequests) {
	return super.registerRequestByType(osRequests, RequestCnts.Type.SIP);
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerSipDao.findIdsSessVersUdDocChiusaOk(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osSipS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.VRS_SESSIONE_VERS;
    }

}
