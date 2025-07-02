/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the ARO_COMP_DOC database table.
 */
@Entity
@Table(name = "ARO_COMP_DOC")
@NamedQuery(name = "AroCompDoc.findAll", query = "SELECT a FROM AroCompDoc a")
public class AroCompDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCompDoc;

    private Long idStrut;

    private List<AroContenutoComp> aroContenutoComps = new ArrayList<>();

    private String tiSupportoComp;

    private AroStrutDoc aroStrutDoc;

    private BigDecimal niOrdCompDoc;

    private AroCompDoc aroCompDoc;

    public AroCompDoc() {
	// hibernate
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
    public Long getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    // bi-directional many-to-one association to AroContenutoComp
    @OneToMany(mappedBy = "aroCompDoc")
    public List<AroContenutoComp> getAroContenutoComps() {
	return this.aroContenutoComps;
    }

    public void setAroContenutoComps(List<AroContenutoComp> aroContenutoComps) {
	this.aroContenutoComps = aroContenutoComps;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
	return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
	this.tiSupportoComp = tiSupportoComp;
    }

    // bi-directional many-to-one association to AroStrutDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT_DOC")
    public AroStrutDoc getAroStrutDoc() {
	return this.aroStrutDoc;
    }

    public void setAroStrutDoc(AroStrutDoc aroStrutDoc) {
	this.aroStrutDoc = aroStrutDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
	return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
	this.niOrdCompDoc = niOrdCompDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMP_DOC_PADRE")
    public AroCompDoc getAroCompDoc() {
	return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
	this.aroCompDoc = aroCompDoc;
    }

}
