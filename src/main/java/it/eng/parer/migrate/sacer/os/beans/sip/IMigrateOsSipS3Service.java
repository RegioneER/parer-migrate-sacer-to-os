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

/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.sip;

import it.eng.parer.migrate.sacer.os.base.IObjectStorageResource;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;

public interface IMigrateOsSipS3Service {

    /**
     * Effettua migrazione per singola sessione di versamento
     *
     * @param idSacerBackend id backend S3
     *
     * @param idSessioneVers sessione di versamento
     * @param delete         cancellazione del dato al termine della migrazione
     *
     * @return risorsa migrata su object storage via S3
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     */
    IObjectStorageResource doMigrate(Long idSacerBackend, Long idSessioneVers, Boolean delete)
	    throws AppMigrateOsS3Exception;

}
