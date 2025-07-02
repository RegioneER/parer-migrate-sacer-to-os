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

import java.math.BigDecimal;

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroUpdDatiSpecUdObjectStorageCnts.TiEntitaAroUpdDatiSpecUdObjectStorage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_UPD_DATI_SPEC_UD_OBJECT_STORAGE")
public class AroUpdDatiSpecUdObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idUpdDatiSpecUdObjectStorage;

    private AroUpdUnitaDoc aroUpdUnitaDoc;

    private AroUpdDocUnitaDoc aroUpdDocUnitaDoc;

    private AroUpdCompUnitaDoc aroUpdCompUnitaDoc;

    private TiEntitaAroUpdDatiSpecUdObjectStorage tiEntitaSacer;

    private BigDecimal idStrut;

    public AroUpdDatiSpecUdObjectStorage() {
	// hibernate constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UPD_DATI_SPEC_UD_OBJECT_STORAGE")
    public Long getIdUpdDatiSpecUdObjectStorage() {
	return idUpdDatiSpecUdObjectStorage;
    }

    public void setIdUpdDatiSpecUdObjectStorage(Long idUpdDatiSpecUdObjectStorage) {
	this.idUpdDatiSpecUdObjectStorage = idUpdDatiSpecUdObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
	return aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
	this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_DOC_UNITA_DOC")
    public AroUpdDocUnitaDoc getAroUpdDocUnitaDoc() {
	return aroUpdDocUnitaDoc;
    }

    public void setAroUpdDocUnitaDoc(AroUpdDocUnitaDoc aroUpdDocUnitaDoc) {
	this.aroUpdDocUnitaDoc = aroUpdDocUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_COMP_UNITA_DOC")
    public AroUpdCompUnitaDoc getAroUpdCompUnitaDoc() {
	return aroUpdCompUnitaDoc;
    }

    public void setAroUpdCompUnitaDoc(AroUpdCompUnitaDoc aroUpdCompUnitaDoc) {
	this.aroUpdCompUnitaDoc = aroUpdCompUnitaDoc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaAroUpdDatiSpecUdObjectStorage getTiEntitaSacer() {
	return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaAroUpdDatiSpecUdObjectStorage tiEntitaSacer) {
	this.tiEntitaSacer = tiEntitaSacer;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }
}
