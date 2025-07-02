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

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.AroVersIniDatiSpecObjectStorageCnts.TiEntitaSacerAroVersIniDatiSpecOs;
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
@Table(name = "ARO_VERS_INI_DATI_SPEC_OBJECT_STORAGE")
public class AroVersIniDatiSpecObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idVersIniDatiSpecObjectStorage;
    private AroVersIniUnitaDoc aroVersIniUnitaDoc;
    private AroVersIniDoc aroVersIniDoc;
    private AroVersIniComp aroVersIniComp;
    private TiEntitaSacerAroVersIniDatiSpecOs tiEntitaSacer;
    private BigDecimal idStrut;

    public AroVersIniDatiSpecObjectStorage() {
	// hibernate constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VERS_INI_DATI_SPEC_OBJECT_STORAGE")
    public Long getIdVersIniDatiSpecObjectStorage() {
	return idVersIniDatiSpecObjectStorage;
    }

    public void setIdVersIniDatiSpecObjectStorage(Long idVersIniDatiSpecObjectStorage) {
	this.idVersIniDatiSpecObjectStorage = idVersIniDatiSpecObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_UNITA_DOC")
    public AroVersIniUnitaDoc getAroVersIniUnitaDoc() {
	return aroVersIniUnitaDoc;
    }

    public void setAroVersIniUnitaDoc(AroVersIniUnitaDoc aroVersIniUnitaDoc) {
	this.aroVersIniUnitaDoc = aroVersIniUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_DOC")
    public AroVersIniDoc getAroVersIniDoc() {
	return aroVersIniDoc;
    }

    public void setAroVersIniDoc(AroVersIniDoc aroVersIniDoc) {
	this.aroVersIniDoc = aroVersIniDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_INI_COMP")
    public AroVersIniComp getAroVersIniComp() {
	return aroVersIniComp;
    }

    public void setAroVersIniComp(AroVersIniComp aroVersIniComp) {
	this.aroVersIniComp = aroVersIniComp;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ENTITA_SACER")
    public TiEntitaSacerAroVersIniDatiSpecOs getTiEntitaSacer() {
	return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(TiEntitaSacerAroVersIniDatiSpecOs tiEntitaSacer) {
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
