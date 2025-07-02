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
import jakarta.persistence.Table;

@Entity
@Table(name = "ORG_AMBIENTE")
public class OrgAmbiente {
    private static final long serialVersionUID = 1L;

    public OrgAmbiente() {/* Hibernate */
    }

    private Long idAmbiente;

    private String nmAmbiente;

    @Id
    @Column(name = "ID_AMBIENTE", insertable = false, updatable = false)
    public Long getIdAmbiente() {
	return idAmbiente;
    }

    public void setIdAmbiente(Long idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }
}
