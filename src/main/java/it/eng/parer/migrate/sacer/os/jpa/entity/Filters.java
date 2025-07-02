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

package it.eng.parer.migrate.sacer.os.jpa.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "FILTERS")
public class Filters implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idRequest;
    private Requests request;
    private Long idUnitadoc;
    private Long idDoc;
    private Long idSessioneVers;
    private Long idStrut;
    private LocalDate dtApertura;
    private Integer dtAperturaYY;
    private Long rowlimit;
    private Long idCompDoc;
    private Long idElencoVers;
    private Long idVerIndiceAip;
    private Long idVerSerie;

    public Filters() {
	// constructor
    }

    @Id
    @Column(name = "ID_REQUEST")
    public Long getIdRequest() {
	return idRequest;
    }

    public void setIdRequest(Long idRequest) {
	this.idRequest = idRequest;
    }

    @MapsId
    @OneToOne(mappedBy = "filter")
    @JoinColumn(name = "ID_REQUEST")
    public Requests getRequest() {
	return request;
    }

    public void setRequest(Requests request) {
	this.request = request;
    }

    @Column(name = "ID_UNITA_DOC", nullable = true)
    public Long getIdUnitadoc() {
	return idUnitadoc;
    }

    public void setIdUnitadoc(Long idUnitadoc) {
	this.idUnitadoc = idUnitadoc;
    }

    @Column(name = "ID_DOC", nullable = true)
    public Long getIdDoc() {
	return idDoc;
    }

    public void setIdDoc(Long idDoc) {
	this.idDoc = idDoc;
    }

    @Column(name = "ID_SESSIONE_VERS", nullable = true)
    public Long getIdSessioneVers() {
	return idSessioneVers;
    }

    public void setIdSessioneVers(Long idSessioneVers) {
	this.idSessioneVers = idSessioneVers;
    }

    @Column(name = "ID_STRUT", nullable = true)
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "DT_APERTURA", nullable = true)
    public LocalDate getDtApertura() {
	return dtApertura;
    }

    public void setDtApertura(LocalDate dtApertura) {
	this.dtApertura = dtApertura;
    }

    @Column(name = "DT_APERTURA_YY", nullable = true)
    public Integer getDtAperturaYY() {
	return dtAperturaYY;
    }

    public void setDtAperturaYY(Integer dtAperturaYY) {
	this.dtAperturaYY = dtAperturaYY;
    }

    @Column(name = "ROWLIMIT", nullable = true)
    public Long getRowlimit() {
	return rowlimit;
    }

    public void setRowlimit(Long rowlimit) {
	this.rowlimit = rowlimit;
    }

    @Column(name = "ID_COMP", nullable = true)
    public Long getIdCompDoc() {
	return idCompDoc;
    }

    public void setIdCompDoc(Long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_ELENCO_VERS", nullable = true)
    public Long getIdElencoVers() {
	return idElencoVers;
    }

    public void setIdElencoVers(Long idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_VER_INDICE_AIP", nullable = true)
    public Long getIdVerIndiceAip() {
	return idVerIndiceAip;
    }

    public void setIdVerIndiceAip(Long idVerIndiceAip) {
	this.idVerIndiceAip = idVerIndiceAip;
    }

    @Column(name = "ID_VER_SERIE", nullable = true)
    public Long getIdVerSerie() {
	return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
	this.idVerSerie = idVerSerie;
    }
}
