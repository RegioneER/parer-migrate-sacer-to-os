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
@Table(name = "SER_VER_SERIE_OBJECT_STORAGE")
public class SerVerSerieObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    public SerVerSerieObjectStorage() {
	super();
    }

    private Long idVerSerieObjectStorage;
    private Long idVerSerie;
    private Long idStrut;
    private String tiFileVerSerie;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VER_SERIE_OBJECT_STORAGE")
    public Long getIdVerSerieObjectStorage() {
	return idVerSerieObjectStorage;
    }

    public void setIdVerSerieObjectStorage(Long idVerSerieObjectStorage) {
	this.idVerSerieObjectStorage = idVerSerieObjectStorage;
    }

    @Column(name = "ID_VER_SERIE")
    public Long getIdVerSerie() {
	return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "TI_FILE_VER_SERIE")
    public String getTiFileVerSerie() {
	return this.tiFileVerSerie;
    }

    public void setTiFileVerSerie(String tiFileVerSerie) {
	this.tiFileVerSerie = tiFileVerSerie;
    }
}
