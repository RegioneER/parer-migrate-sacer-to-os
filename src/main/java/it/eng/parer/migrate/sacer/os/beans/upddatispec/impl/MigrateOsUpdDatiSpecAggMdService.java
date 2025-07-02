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

package it.eng.parer.migrate.sacer.os.beans.upddatispec.impl;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.base.MigrateOsAbstract;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.IMigrateOsUpdDatiSpecAggMdS3Service;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.IMigrateOsUpdDatiSpecAggMdService;
import it.eng.parer.migrate.sacer.os.beans.upddatispec.ISacerUpdDatiSpecAggMdDao;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts.ObjectType;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MigrateOsUpdDatiSpecAggMdService extends MigrateOsAbstract
	implements IMigrateOsUpdDatiSpecAggMdService {

    @Inject
    ISacerUpdDatiSpecAggMdDao sacerUpdDatiSpecAggMdDao;

    @Inject
    IMigrateOsUpdDatiSpecAggMdS3Service osS3Service;

    @Override
    public void processMigrationUpdDatiSpecAggMdFromRequest(Long idRequest) {
	super.processMigrationRequest(idRequest);
    }

    @Override
    public List<RequestDto> registerMigrationUpdDatiSpecAggMdRequest(
	    List<MigrateRequest> osUpdDatiSpecAggMdRequests) {
	return super.registerRequestByType(osUpdDatiSpecAggMdRequests,
		RequestCnts.Type.UPD_DATI_SPEC_INI);
    }

    @Override
    protected ObjectType getObjType() {
	return ObjectStorageCnts.ObjectType.ARO_UPD_DATI_SPEC_UNITA_DOC;
    }

    @Override
    protected Stream<Long> findObjIdsByFilter(FilterDto filter) {
	return sacerUpdDatiSpecAggMdDao.findIdsUpdDatiSpecAggMd(filter);
    }

    @Override
    protected IObjectStorageResource executeMigrateViaS3(Long idSacerBackend, Long objId,
	    Boolean deleteSrc) throws AppMigrateOsS3Exception {
	return osS3Service.doMigrate(idSacerBackend, objId, deleteSrc);
    }

}
