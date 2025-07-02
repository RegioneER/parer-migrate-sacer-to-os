/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.migrate.sacer.os.beans.datispecvers.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.IMigrateOsDatiSpecVersS3Service;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.IMigrateOsDatiSpecVersService;
import it.eng.parer.migrate.sacer.os.beans.datispecvers.ISacerDatiSpecVersDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsDatiSpecVersService extends MigrateOsAbstract
	implements IMigrateOsDatiSpecVersService {

    @Inject
    ISacerDatiSpecVersDao sacerDatiSpecVersDao;

    @Inject
    IMigrateOsDatiSpecVersS3Service osS3Service;

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerDatiSpecVersDao.findIdsDatiSpecVers(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ARO_VERS_INI_DATI_SPEC;
    }

    @Override
    public void processMigrationDatiSpecVersFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationDatiSpecVersRequest(
	    List<MigrateRequest> osDatiSpecVersRequests) {
	return super.registerRequestByType(osDatiSpecVersRequests, RequestCnts.Type.DATI_SPEC_VERS);
    }

}
