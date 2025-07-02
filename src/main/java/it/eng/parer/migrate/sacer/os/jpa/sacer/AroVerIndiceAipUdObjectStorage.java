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

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_VER_INDICE_AIP_UD_OBJECT_STORAGE")
public class AroVerIndiceAipUdObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idVerIndiceAipUdObjectStorage;

    private Long idVerIndiceAipUd;

    private Long idSubStrut;

    private BigDecimal aaKeyUnitaDoc;

    public AroVerIndiceAipUdObjectStorage() {
	super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VER_INDICE_AIP_UD_OBJECT_STORAGE")
    public Long getIdVerIndiceAipUdObjectStorage() {
	return idVerIndiceAipUdObjectStorage;
    }

    public void setIdVerIndiceAipUdObjectStorage(Long idVerIndiceAipUdObjectStorage) {
	this.idVerIndiceAipUdObjectStorage = idVerIndiceAipUdObjectStorage;
    }

    @Column(name = "ID_VER_INDICE_AIP")
    public Long getIdVerIndiceAipUd() {
	return idVerIndiceAipUd;
    }

    public void setIdVerIndiceAipUd(Long idVerIndiceAipUd) {
	this.idVerIndiceAipUd = idVerIndiceAipUd;
    }

    @Column(name = "ID_SUB_STRUT")
    public Long getIdSubStrut() {
	return idSubStrut;
    }

    public void setIdSubStrut(Long idSubStrut) {
	this.idSubStrut = idSubStrut;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

}
