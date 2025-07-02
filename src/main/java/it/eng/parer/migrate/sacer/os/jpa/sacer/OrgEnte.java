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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORG_ENTE")
public class OrgEnte {
    private static final long serialVersionUID = 1L;

    public OrgEnte() {/* Hibernate */
    }

    private Long idEnte;

    private String nmEnte;

    private OrgAmbiente orgAmbiente;

    @Id
    @Column(name = "ID_ENTE", insertable = false, updatable = false)
    public Long getIdEnte() {
	return idEnte;
    }

    public void setIdEnte(Long idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @OneToOne
    @JoinColumn(name = "ID_AMBIENTE")
    public OrgAmbiente getOrgAmbiente() {
	return orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
	this.orgAmbiente = orgAmbiente;
    }
}
