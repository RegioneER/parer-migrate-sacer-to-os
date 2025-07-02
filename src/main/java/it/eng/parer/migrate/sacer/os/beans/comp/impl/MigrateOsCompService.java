package it.eng.parer.migrate.sacer.os.beans.comp.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.comp.IMigrateOsCompS3Service;
import it.eng.parer.migrate.sacer.os.beans.comp.IMigrateOsCompService;
import it.eng.parer.migrate.sacer.os.beans.comp.ISacerCompDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsCompService extends MigrateOsAbstract implements IMigrateOsCompService {

    @ConfigProperty(name = "parer.migrate.sacer.os.comp.ids-from-view.enabled")
    boolean idsFromViewEnabled;

    @Inject
    ISacerCompDao sacerCompDao;

    @Inject
    IMigrateOsCompS3Service osCompS3Service;

    @Override
    public void processMigrationCompFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationCompRequest(List<MigrateRequest> osCompRequests) {
	return super.registerRequestByType(osCompRequests, RequestCnts.Type.COMP);
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	if (idsFromViewEnabled) {
	    Objects.requireNonNull(filter.getIdStrut(),
		    "When parer.migrate.sacer.os.comp.ids-from-view.enabled is enabled (=true), filter.idstrut is REQUIRED!");
	    return sacerCompDao.findIdsCompDocOnView(filter);
	}
	return sacerCompDao.findIdsCompDoc(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osCompS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ARO_COMP_DOC;
    }

}
