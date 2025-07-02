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

package it.eng.parer.migrate.sacer.os.jpa.constraint;

public final class ObjectStorageCnts {

    private ObjectStorageCnts() {
    }

    public enum State {
	TO_MIGRATE, MIGRATED, MIGRATION_ERROR
    }

    public enum IntegrityType {
	MD5, SHA256, CRC32C
    }

    public enum ObjectType {
	VRS_SESSIONE_VERS, ARO_COMP_DOC, ARO_INDICE_AIP_UD, ARO_UPD_UNITA_DOC, ELENCO_INDICI_AIP,
	SER_FILE_VER_SERIE_AIP, INDICE_ELENCO_VERS, ARO_UPD_DATI_SPEC_UNITA_DOC,
	ARO_VERS_INI_DATI_SPEC
    }

}
