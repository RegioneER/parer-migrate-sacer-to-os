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
import java.time.LocalDate;

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroVersIniDatiSpecCnts.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroVersIniDatiSpecCnts.TiUsoXsdAroVersIniDatiSpec;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * The persistent class for the ARO_VERS_INI_DATI_SPEC database table.
 */
@Entity
@Table(name = "ARO_VERS_INI_DATI_SPEC")
public class AroVersIniDatiSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVersIniDatiSpec;

    private String blXmlDatiSpec;

    private LocalDate dtReg;

    private OrgStrut orgStrut;

    private Long idVersIniComp;

    private Long idVersIniDoc;

    private AroVersIniDoc aroVersIniDoc;

    private AroVersIniComp aroVersIniComp;

    private AroVersIniUnitaDoc aroVersIniUnitaDoc;

    private TiEntitaSacerAroVersIniDatiSpec tiEntitaSacer;

    private TiUsoXsdAroVersIniDatiSpec tiUsoXsd;

    private Integer aaDtReg;

    public AroVersIniDatiSpec() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VERS_INI_DATI_SPEC")
    public Long getIdVersIniDatiSpec() {
	return this.idVersIniDatiSpec;
    }

    public void setIdVersIniDatiSpec(Long idVersIniDatiSpec) {
	this.idVersIniDatiSpec = idVersIniDatiSpec;
    }

    @Lob
    @Column(name = "BL_XML_DATI_SPEC")
    public String getBlXmlDatiSpec() {
	return this.blXmlDatiSpec;
    }

    public void setBlXmlDatiSpec(String blXmlDatiSpec) {
	this.blXmlDatiSpec = blXmlDatiSpec;
    }

    @Column(name = "DT_REG")
    public LocalDate getDtReg() {
	return this.dtReg;
    }

    public void setDtReg(LocalDate dtReg) {
	this.dtReg = dtReg;
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

    @Column(name = "ID_VERS_INI_COMP")
    public Long getIdVersIniComp() {
	return this.idVersIniComp;
    }

    public void setIdVersIniComp(Long idVersIniComp) {
	this.idVersIniComp = idVersIniComp;
    }

    @Column(name = "ID_VERS_INI_DOC")
    public Long getIdVersIniDoc() {
	return this.idVersIniDoc;
    }

    public void setIdVersIniDoc(Long idVersIniDoc) {
	this.idVersIniDoc = idVersIniDoc;
    }

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_UNITA_DOC")
    public AroVersIniUnitaDoc getAroVersIniUnitaDoc() {
	return this.aroVersIniUnitaDoc;
    }

    public void setAroVersIniUnitaDoc(AroVersIniUnitaDoc aroVersIniUnitaDoc) {
	this.aroVersIniUnitaDoc = aroVersIniUnitaDoc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaSacerAroVersIniDatiSpec getTiEntitaSacer() {
	return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaSacerAroVersIniDatiSpec tiEntitaSacer) {
	this.tiEntitaSacer = tiEntitaSacer;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_USO_XSD")
    public TiUsoXsdAroVersIniDatiSpec getTiUsoXsd() {
	return this.tiUsoXsd;
    }

    public void setTiUsoXsd(TiUsoXsdAroVersIniDatiSpec tiUsoXsd) {
	this.tiUsoXsd = tiUsoXsd;
    }

    @Column(name = "AA_DT_REG")
    public Integer getAaDtReg() {
	return aaDtReg;
    }

    public void setAaDtReg(Integer aaDtReg) {
	this.aaDtReg = aaDtReg;
    }

    @OneToOne
    @JoinColumn(name = "ID_VERS_INI_DOC", insertable = false, updatable = false)
    public AroVersIniDoc getAroVersIniDoc() {
	return aroVersIniDoc;
    }

    public void setAroVersIniDoc(AroVersIniDoc aroVersIniDoc) {
	this.aroVersIniDoc = aroVersIniDoc;
    }

    @OneToOne
    @JoinColumn(name = "ID_VERS_INI_COMP", insertable = false, updatable = false)
    public AroVersIniComp getAroVersIniComp() {
	return aroVersIniComp;
    }

    public void setAroVersIniComp(AroVersIniComp aroVersIniComp) {
	this.aroVersIniComp = aroVersIniComp;
    }
}
