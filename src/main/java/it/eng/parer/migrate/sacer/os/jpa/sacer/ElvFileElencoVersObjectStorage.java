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

package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ELV_FILE_ELENCO_VERS_OBJECT_STORAGE")
public class ElvFileElencoVersObjectStorage extends AroObjectStorage {
    private static final long serialVersionUID = 1L;

    private Long idFileElencoVersObjectStorage;

    private Long idFileElencoVers;

    private Long idStrut;

    public ElvFileElencoVersObjectStorage() {
	super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FILE_ELENCO_VERS_OBJECT_STORAGE")
    public Long getIdFileElencoVersObjectStorage() {
	return idFileElencoVersObjectStorage;
    }

    public void setIdFileElencoVersObjectStorage(Long idFileElencoVersObjectStorage) {
	this.idFileElencoVersObjectStorage = idFileElencoVersObjectStorage;
    }

    @Column(name = "ID_FILE_ELENCO_VERS")
    public Long getIdFileElencoVers() {
	return idFileElencoVers;
    }

    public void setIdFileElencoVers(Long idFileElencoVers) {
	this.idFileElencoVers = idFileElencoVers;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }
}
