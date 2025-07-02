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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the ARO_UPD_DOC_UNITA_DOC database table.
 */
@Entity
@Table(name = "ARO_UPD_DOC_UNITA_DOC")
public class AroUpdDocUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdDocUnitaDoc;

    private String dlDoc;

    private String dsAutoreDoc;

    private AroDoc aroDoc;

    private AroUpdUnitaDoc aroUpdUnitaDoc;

    private List<AroUpdCompUnitaDoc> aroUpdCompUnitaDocs = new ArrayList<>();

    private String flUpdProfiloDoc;

    private String flUpdDatiSpec;

    private String flUpdDatiSpecMigraz;

    public AroUpdDocUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UPD_DOC_UNITA_DOC")
    public Long getIdUpdDocUnitaDoc() {
	return this.idUpdDocUnitaDoc;
    }

    public void setIdUpdDocUnitaDoc(Long idUpdDocUnitaDoc) {
	this.idUpdDocUnitaDoc = idUpdDocUnitaDoc;
    }

    @Column(name = "DL_DOC")
    public String getDlDoc() {
	return this.dlDoc;
    }

    public void setDlDoc(String dlDoc) {
	this.dlDoc = dlDoc;
    }

    @Column(name = "DS_AUTORE_DOC")
    public String getDsAutoreDoc() {
	return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
	this.dsAutoreDoc = dsAutoreDoc;
    }

    // bi-directional many-to-one association to AroDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOC")
    public AroDoc getAroDoc() {
	return this.aroDoc;
    }

    public void setAroDoc(AroDoc aroDoc) {
	this.aroDoc = aroDoc;
    }

    // bi-directional many-to-one association to AroDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
	return this.aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
	this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }

    // bi-directional many-to-one association to AroUpdCompUnitaDoc
    @OneToMany(mappedBy = "aroUpdDocUnitaDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<AroUpdCompUnitaDoc> getAroUpdCompUnitaDocs() {
	return this.aroUpdCompUnitaDocs;
    }

    public void setAroUpdCompUnitaDocs(List<AroUpdCompUnitaDoc> aroUpdCompUnitaDocs) {
	this.aroUpdCompUnitaDocs = aroUpdCompUnitaDocs;
    }

    @Column(name = "FL_UPD_PROFILO_DOC", columnDefinition = "char(1)")
    public String getFlUpdProfiloDoc() {
	return this.flUpdProfiloDoc;
    }

    public void setFlUpdProfiloDoc(String flUpdProfiloDoc) {
	this.flUpdProfiloDoc = flUpdProfiloDoc;
    }

    @Column(name = "FL_UPD_DATI_SPEC", columnDefinition = "char(1)")
    public String getFlUpdDatiSpec() {
	return this.flUpdDatiSpec;
    }

    public void setFlUpdDatiSpec(String flUpdDatiSpec) {
	this.flUpdDatiSpec = flUpdDatiSpec;
    }

    @Column(name = "FL_UPD_DATI_SPEC_MIGRAZ", columnDefinition = "char(1)")
    public String getFlUpdDatiSpecMigraz() {
	return this.flUpdDatiSpecMigraz;
    }

    public void setFlUpdDatiSpecMigraz(String flUpdDatiSpecMigraz) {
	this.flUpdDatiSpecMigraz = flUpdDatiSpecMigraz;
    }
}
