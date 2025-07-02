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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

/**
 * The persistent class for the SER_SERIE database table.
 */
@Entity
@Table(name = "SER_SERIE")

public class SerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSerie;

    private String cdCompositoSerie;

    public SerSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_SERIE")
    public Long getIdSerie() {
	return this.idSerie;
    }

    public void setIdSerie(Long idSerie) {
	this.idSerie = idSerie;
    }

    @Column(name = "CD_COMPOSITO_SERIE")
    public String getCdCompositoSerie() {
	return this.cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
	this.cdCompositoSerie = cdCompositoSerie;
    }

}
