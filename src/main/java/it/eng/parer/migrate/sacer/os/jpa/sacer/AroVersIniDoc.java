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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

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
 * The persistent class for the ARO_VERS_INI_DOC database table.
 */
@Entity
@Table(name = "ARO_VERS_INI_DOC")
public class AroVersIniDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVersIniDoc;

    private String dlDoc;

    private String dsAutoreDoc;

    private AroDoc aroDoc;

    private AroVersIniUnitaDoc aroVersIniUnitaDoc;

    private List<AroVersIniDatiSpec> aroVersIniDatiSpecs = new ArrayList<>();

    private List<AroVersIniComp> aroVersIniComps = new ArrayList<>();

    public AroVersIniDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VERS_INI_DOC")
    public Long getIdVersIniDoc() {
	return this.idVersIniDoc;
    }

    public void setIdVersIniDoc(Long idVersIniDoc) {
	this.idVersIniDoc = idVersIniDoc;
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

    // bi-directional many-to-one association to AroVersIniUnitaDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_UNITA_DOC")
    public AroVersIniUnitaDoc getAroVersIniUnitaDoc() {
	return this.aroVersIniUnitaDoc;
    }

    public void setAroVersIniUnitaDoc(AroVersIniUnitaDoc aroVersIniUnitaDoc) {
	this.aroVersIniUnitaDoc = aroVersIniUnitaDoc;
    }

    // bi-directional many-to-one association to AroVersIniDoc
    @OneToMany(mappedBy = "aroVersIniDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<AroVersIniDatiSpec> getAroVersIniDatiSpecs() {
	return this.aroVersIniDatiSpecs;
    }

    public void setAroVersIniDatiSpecs(List<AroVersIniDatiSpec> aroVersIniDatiSpecs) {
	this.aroVersIniDatiSpecs = aroVersIniDatiSpecs;
    }

    // bi-directional many-to-one association to AroVersIniComp
    @OneToMany(mappedBy = "aroVersIniDoc")
    public List<AroVersIniComp> getAroVersIniComps() {
	return this.aroVersIniComps;
    }

    public void setAroVersIniComps(List<AroVersIniComp> aroVersIniComps) {
	this.aroVersIniComps = aroVersIniComps;
    }
}
