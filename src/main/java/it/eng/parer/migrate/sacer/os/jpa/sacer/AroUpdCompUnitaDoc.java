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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The persistent class for the ARO_UPD_COMP_UNITA_DOC database table.
 */
@Entity
@Table(name = "ARO_UPD_COMP_UNITA_DOC")
public class AroUpdCompUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdCompUnitaDoc;

    private String dlUrnCompVers;

    private String dsIdCompVers;

    private String dsNomeCompVers;

    private AroCompDoc aroCompDoc;

    private AroUpdDocUnitaDoc aroUpdDocUnitaDoc;

    private String flUpdDatiSpec;

    private String flUpdDatiSpecMigraz;

    public AroUpdCompUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UPD_COMP_UNITA_DOC")
    public Long getIdUpdCompUnitaDoc() {
	return this.idUpdCompUnitaDoc;
    }

    public void setIdUpdCompUnitaDoc(Long idUpdCompUnitaDoc) {
	this.idUpdCompUnitaDoc = idUpdCompUnitaDoc;
    }

    @Column(name = "DL_URN_COMP_VERS")
    public String getDlUrnCompVers() {
	return this.dlUrnCompVers;
    }

    public void setDlUrnCompVers(String dlUrnCompVers) {
	this.dlUrnCompVers = dlUrnCompVers;
    }

    @Column(name = "DS_ID_COMP_VERS")
    public String getDsIdCompVers() {
	return this.dsIdCompVers;
    }

    public void setDsIdCompVers(String dsIdCompVers) {
	this.dsIdCompVers = dsIdCompVers;
    }

    @Column(name = "DS_NOME_COMP_VERS")
    public String getDsNomeCompVers() {
	return this.dsNomeCompVers;
    }

    public void setDsNomeCompVers(String dsNomeCompVers) {
	this.dsNomeCompVers = dsNomeCompVers;
    }

    // bi-directional many-to-one association to AroDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMP_DOC")
    public AroCompDoc getAroCompDoc() {
	return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
	this.aroCompDoc = aroCompDoc;
    }

    // bi-directional many-to-one association to AroDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_DOC_UNITA_DOC")
    public AroUpdDocUnitaDoc getAroUpdDocUnitaDoc() {
	return this.aroUpdDocUnitaDoc;
    }

    public void setAroUpdDocUnitaDoc(AroUpdDocUnitaDoc aroUpdDocUnitaDoc) {
	this.aroUpdDocUnitaDoc = aroUpdDocUnitaDoc;
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
