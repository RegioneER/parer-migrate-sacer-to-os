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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * The persistent class for the ARO_VERS_INI_UNITA_DOC database table.
 */
@Entity
@Table(name = "ARO_VERS_INI_UNITA_DOC")
public class AroVersIniUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVersIniUnitaDoc;

    private AroUnitaDoc aroUnitaDoc;

    private List<AroVersIniDatiSpec> aroVersIniDatiSpecs = new ArrayList<>();

    private List<AroVersIniDoc> aroVersIniDocs = new ArrayList<>();

    public AroVersIniUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VERS_INI_UNITA_DOC")
    public Long getIdVersIniUnitaDoc() {
	return this.idVersIniUnitaDoc;
    }

    public void setIdVersIniUnitaDoc(Long idVersIniUnitaDoc) {
	this.idVersIniUnitaDoc = idVersIniUnitaDoc;
    }

    // bi-directional one-to-one association to AroUnitaDoc
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    // bi-directional many-to-one association to AroVersIniDoc
    @OneToMany(mappedBy = "aroVersIniUnitaDoc")
    public List<AroVersIniDatiSpec> getAroVersIniDatiSpecs() {
	return this.aroVersIniDatiSpecs;
    }

    public void setAroVersIniDatiSpecs(List<AroVersIniDatiSpec> aroVersIniDatiSpecs) {
	this.aroVersIniDatiSpecs = aroVersIniDatiSpecs;
    }

    // bi-directional many-to-one association to AroVersIniDoc
    @OneToMany(mappedBy = "aroVersIniUnitaDoc")
    public List<AroVersIniDoc> getAroVersIniDocs() {
	return this.aroVersIniDocs;
    }

    public void setAroVersIniDocs(List<AroVersIniDoc> aroVersIniDocs) {
	this.aroVersIniDocs = aroVersIniDocs;
    }
}
