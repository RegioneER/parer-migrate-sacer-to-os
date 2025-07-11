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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.migrate.sacer.os.jpa.sacer.sequence.NonMonotonicSequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the VRS_SESSIONE_VERS database table.
 */
@Entity
@Table(name = "VRS_SESSIONE_VERS")
public class VrsSessioneVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSessioneVers;

    private BigDecimal aaKeyUnitaDoc;

    private String cdErrPrinc;

    private String cdKeyDocVers;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private String cdVersioneWs;

    private String dsErrPrinc;

    private LocalDate dtApertura;

    private LocalDate dtChiusura;

    private LocalDateTime tsApertura;

    private LocalDateTime tsChiusura;

    private String flSessioneErrNonRisolub;

    private String flSessioneErrVerif;

    private BigDecimal niFileErr;

    private String nmAmbiente;

    private String nmEnte;

    private String nmStrut;

    private String nmUserid;

    private String nmUseridWs;

    private String nmUtente;

    private String tiSessioneVers;

    private String tiStatoSessioneVers;

    private String cdIndIpClient;

    private String cdIndServer;

    private List<VrsDatiSessioneVers> vrsDatiSessioneVers = new ArrayList<>();

    private AroDoc aroDoc;

    private AroUnitaDoc aroUnitaDoc;

    private Long orgStrut;

    private Long iamUser;

    public VrsSessioneVers() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_SESSIONE_VERS")
    @GenericGenerator(name = "SVRS_SESSIONE_VERS_ID_SESSIONE_VERS_GENERATOR", type = NonMonotonicSequenceGenerator.class, parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVRS_SESSIONE_VERS"),
	    @Parameter(name = OptimizableGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVRS_SESSIONE_VERS_ID_SESSIONE_VERS_GENERATOR")
    public Long getIdSessioneVers() {
	return this.idSessioneVers;
    }

    public void setIdSessioneVers(Long idSessioneVers) {
	this.idSessioneVers = idSessioneVers;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
	return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
	this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_KEY_DOC_VERS")
    public String getCdKeyDocVers() {
	return this.cdKeyDocVers;
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
	this.cdKeyDocVers = cdKeyDocVers;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
	return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
	return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
	this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
	return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
	this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "DT_APERTURA")
    public LocalDate getDtApertura() {
	return this.dtApertura;
    }

    public void setDtApertura(LocalDate dtApertura) {
	this.dtApertura = dtApertura;
    }

    @Column(name = "DT_CHIUSURA")
    public LocalDate getDtChiusura() {
	return this.dtChiusura;
    }

    public void setDtChiusura(LocalDate dtChiusura) {
	this.dtChiusura = dtChiusura;
    }

    @Column(name = "TS_APERTURA")
    public LocalDateTime getTsApertura() {
	return tsApertura;
    }

    public void setTsApertura(LocalDateTime tsApertura) {
	this.tsApertura = tsApertura;
    }

    @Column(name = "TS_CHIUSURA")
    public LocalDateTime getTsChiusura() {
	return tsChiusura;
    }

    public void setTsChiusura(LocalDateTime tsChiusura) {
	this.tsChiusura = tsChiusura;
    }

    @Column(name = "FL_SESSIONE_ERR_NON_RISOLUB", columnDefinition = "char(1)")
    public String getFlSessioneErrNonRisolub() {
	return this.flSessioneErrNonRisolub;
    }

    public void setFlSessioneErrNonRisolub(String flSessioneErrNonRisolub) {
	this.flSessioneErrNonRisolub = flSessioneErrNonRisolub;
    }

    @Column(name = "FL_SESSIONE_ERR_VERIF", columnDefinition = "char(1)")
    public String getFlSessioneErrVerif() {
	return this.flSessioneErrVerif;
    }

    public void setFlSessioneErrVerif(String flSessioneErrVerif) {
	this.flSessioneErrVerif = flSessioneErrVerif;
    }

    @Column(name = "NI_FILE_ERR")
    public BigDecimal getNiFileErr() {
	return this.niFileErr;
    }

    public void setNiFileErr(BigDecimal niFileErr) {
	this.niFileErr = niFileErr;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
	return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
	this.nmUserid = nmUserid;
    }

    @Column(name = "NM_USERID_WS")
    public String getNmUseridWs() {
	return this.nmUseridWs;
    }

    public void setNmUseridWs(String nmUseridWs) {
	this.nmUseridWs = nmUseridWs;
    }

    @Column(name = "TI_SESSIONE_VERS")
    public String getTiSessioneVers() {
	return this.tiSessioneVers;
    }

    public void setTiSessioneVers(String tiSessioneVers) {
	this.tiSessioneVers = tiSessioneVers;
    }

    @Column(name = "TI_STATO_SESSIONE_VERS")
    public String getTiStatoSessioneVers() {
	return this.tiStatoSessioneVers;
    }

    public void setTiStatoSessioneVers(String tiStatoSessioneVers) {
	this.tiStatoSessioneVers = tiStatoSessioneVers;
    }

    @Column(name = "CD_IND_IP_CLIENT")
    public String getCdIndIpClient() {
	return cdIndIpClient;
    }

    public void setCdIndIpClient(String cdIndIpClient) {
	this.cdIndIpClient = cdIndIpClient;
    }

    @Column(name = "CD_IND_SERVER")
    public String getCdIndServer() {
	return cdIndServer;
    }

    public void setCdIndServer(String cdIndServer) {
	this.cdIndServer = cdIndServer;
    }

    // bi-directional many-to-one association to VrsDatiSessioneVers
    @OneToMany(mappedBy = "vrsSessioneVers")
    public List<VrsDatiSessioneVers> getVrsDatiSessioneVers() {
	return this.vrsDatiSessioneVers;
    }

    public void setVrsDatiSessioneVers(List<VrsDatiSessioneVers> vrsDatiSessioneVers) {
	this.vrsDatiSessioneVers = vrsDatiSessioneVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOC")
    public AroDoc getAroDoc() {
	return this.aroDoc;
    }

    public void setAroDoc(AroDoc aroDoc) {
	this.aroDoc = aroDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    // bi-directional many-to-one association to OrgStrut
    @Column(name = "ID_STRUT")
    public Long getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(Long orgStrut) {
	this.orgStrut = orgStrut;
    }

    // bi-directional many-to-one association to IamUser
    @Column(name = "ID_USER")
    public Long getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(Long iamUser) {
	this.iamUser = iamUser;
    }

    @Column(name = "NM_UTENTE")
    public String getNmUtente() {
	return this.nmUtente;
    }

    public void setNmUtente(String nmUtente) {
	this.nmUtente = nmUtente;
    }

}
