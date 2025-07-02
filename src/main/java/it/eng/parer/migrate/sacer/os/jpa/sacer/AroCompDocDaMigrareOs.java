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
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_COMP_DOC_DA_MIGRARE_OS")
public class AroCompDocDaMigrareOs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCompDoc;

    private Long idContenComp;

    private BigDecimal idStrut;

    public AroCompDocDaMigrareOs() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public Long getIdCompDoc() {
	return this.idCompDoc;
    }

    public void setIdCompDoc(Long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_CONTEN_COMP")
    public Long getIdContenComp() {
	return this.idContenComp;
    }

    public void setIdContenComp(Long idContenComp) {
	this.idContenComp = idContenComp;
    }

}
