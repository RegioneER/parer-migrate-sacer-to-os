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

import java.io.Serializable;
import java.sql.Blob;

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.SerFileVerSerieCnts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The persistent class for the SER_FILE_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_FILE_VER_SERIE")
public class SerFileVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileVerSerie;

    private transient Blob blFile;

    private String cdVerXsdFile;

    private Long idStrut;

    private SerFileVerSerieCnts.TiFileVerSerie tiFileVerSerie;

    private SerVerSerie serVerSerie;

    public SerFileVerSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_FILE_VER_SERIE")
    public Long getIdFileVerSerie() {
	return this.idFileVerSerie;
    }

    public void setIdFileVerSerie(Long idFileVerSerie) {
	this.idFileVerSerie = idFileVerSerie;
    }

    @Lob()
    @Column(name = "BL_FILE")
    public Blob getBlFile() {
	return this.blFile;
    }

    public void setBlFile(Blob blFile) {
	this.blFile = blFile;
    }

    @Column(name = "CD_VER_XSD_FILE")
    public String getCdVerXsdFile() {
	return this.cdVerXsdFile;
    }

    public void setCdVerXsdFile(String cdVerXsdFile) {
	this.cdVerXsdFile = cdVerXsdFile;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_FILE_VER_SERIE", nullable = false)
    public SerFileVerSerieCnts.TiFileVerSerie getTiFileVerSerie() {
	return this.tiFileVerSerie;
    }

    public void setTiFileVerSerie(SerFileVerSerieCnts.TiFileVerSerie tiFileVerSerie) {
	this.tiFileVerSerie = tiFileVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
	return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
	this.serVerSerie = serVerSerie;
    }
}
