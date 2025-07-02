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
import java.time.LocalDate;

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUnitaDocCnts.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUnitaDocCnts.TiUsoXsdAroUpdDatiSpecUnitaDoc;
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

@Entity
@Table(name = "ARO_UPD_DATI_SPEC_UNITA_DOC")
public class AroUpdDatiSpecUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdDatiSpecUnitaDoc;

    private String blXmlDatiSpec;

    private LocalDate dtIniSes;

    private OrgStrut orgStrut;

    private Long idUpdCompUnitaDoc;

    private Long idUpdDocUnitaDoc;

    private AroUpdUnitaDoc aroUpdUnitaDoc;

    private TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacer;

    private TiUsoXsdAroUpdDatiSpecUnitaDoc tiUsoXsd;

    public AroUpdDatiSpecUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UPD_DATI_SPEC_UNITA_DOC")
    public Long getIdUpdDatiSpecUnitaDoc() {
	return this.idUpdDatiSpecUnitaDoc;
    }

    public void setIdUpdDatiSpecUnitaDoc(Long idUpdDatiSpecUnitaDoc) {
	this.idUpdDatiSpecUnitaDoc = idUpdDatiSpecUnitaDoc;
    }

    @Lob()
    @Column(name = "BL_XML_DATI_SPEC")
    public String getBlXmlDatiSpec() {
	return this.blXmlDatiSpec;
    }

    public void setBlXmlDatiSpec(String blXmlDatiSpec) {
	this.blXmlDatiSpec = blXmlDatiSpec;
    }

    @Column(name = "DT_INI_SES")
    public LocalDate getDtIniSes() {
	return this.dtIniSes;
    }

    public void setDtIniSes(LocalDate dtIniSes) {
	this.dtIniSes = dtIniSes;
    }

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @Column(name = "ID_UPD_COMP_UNITA_DOC")
    public Long getIdUpdCompUnitaDoc() {
	return this.idUpdCompUnitaDoc;
    }

    public void setIdUpdCompUnitaDoc(Long idUpdCompUnitaDoc) {
	this.idUpdCompUnitaDoc = idUpdCompUnitaDoc;
    }

    @Column(name = "ID_UPD_DOC_UNITA_DOC")
    public Long getIdUpdDocUnitaDoc() {
	return this.idUpdDocUnitaDoc;
    }

    public void setIdUpdDocUnitaDoc(Long idUpdDocUnitaDoc) {
	this.idUpdDocUnitaDoc = idUpdDocUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
	return this.aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
	this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaAroUpdDatiSpecUnitaDoc getTiEntitaSacer() {
	return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacer) {
	this.tiEntitaSacer = tiEntitaSacer;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_USO_XSD")
    public TiUsoXsdAroUpdDatiSpecUnitaDoc getTiUsoXsd() {
	return tiUsoXsd;
    }

    public void setTiUsoXsd(TiUsoXsdAroUpdDatiSpecUnitaDoc tiUsoXsd) {
	this.tiUsoXsd = tiUsoXsd;
    }
}
