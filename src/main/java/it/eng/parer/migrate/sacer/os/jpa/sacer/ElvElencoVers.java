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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "ELV_ELENCO_VERS")
@NamedQuery(name = "ElvElencoVers.findAll", query = "SELECT e FROM ElvElencoVers e")
public class ElvElencoVers {
    private static final long serialVersionUID = 1L;

    public ElvElencoVers() {/* Hibernate */
    }

    private Long idElencoVers;

    private Long idStrut;

    private Date dtCreazioneElenco;

    private List<ElvFileElencoVers> elvFileElencoVers;

    private List<AroUnitaDoc> aroUnitaDocs = new ArrayList<>();

    @Id
    @Column(name = "ID_ELENCO_VERS")
    public Long getIdElencoVers() {
	return idElencoVers;
    }

    public void setIdElencoVers(Long idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_STRUT", insertable = false, updatable = false)
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO")
    public Date getDtCreazioneElenco() {
	return this.dtCreazioneElenco;
    }

    public void setDtCreazioneElenco(Date dtCreazioneElenco) {
	this.dtCreazioneElenco = dtCreazioneElenco;
    }

    @OneToMany(mappedBy = "elvElencoVers")
    public List<ElvFileElencoVers> getElvFileElencoVers() {
	return this.elvFileElencoVers;
    }

    public void setElvFileElencoVers(List<ElvFileElencoVers> elvFileElencoVers) {
	this.elvFileElencoVers = elvFileElencoVers;
    }

    @OneToMany(mappedBy = "elvElencoVers")
    public List<AroUnitaDoc> getAroUnitaDocs() {
	return this.aroUnitaDocs;
    }

    public void setAroUnitaDocs(List<AroUnitaDoc> aroUnitaDocs) {
	this.aroUnitaDocs = aroUnitaDocs;
    }
}
