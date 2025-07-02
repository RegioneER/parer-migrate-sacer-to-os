package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "ARO_UNITA_DOC")
@NamedQuery(name = "AroUnitaDoc.findAll", query = "SELECT a FROM AroUnitaDoc a")
public class AroUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUnitaDoc;

    private BigDecimal aaKeyUnitaDoc;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private Long idStrut;

    private Date dtCreazione;

    private List<AroDoc> aroDocs = new ArrayList<>();

    private List<VrsSessioneVers> vrsSessioneVers = new ArrayList<>();

    private ElvElencoVers elvElencoVers;

    private Long idSubStrut;

    private AroXmlUnitaDocObjectStorage aroXmlUnitaDocObjectStorage;

    private List<AroVersIniUnitaDoc> aroVersIniUnitaDocs = new ArrayList<>();

    public AroUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public Long getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
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

    // bi-directional many-to-one association to AroDoc
    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroDoc> getAroDocs() {
	return this.aroDocs;
    }

    public void setAroDocs(List<AroDoc> aroDocs) {
	this.aroDocs = aroDocs;
    }

    @Column(name = "ID_STRUT", insertable = false, updatable = false)
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<VrsSessioneVers> getVrsSessioneVers() {
	return this.vrsSessioneVers;
    }

    public void setVrsSessioneVers(List<VrsSessioneVers> vrsSessioneVers) {
	this.vrsSessioneVers = vrsSessioneVers;
    }

    @OneToOne
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVers getElvElencoVers() {
	return elvElencoVers;
    }

    public void setElvElencoVers(ElvElencoVers elvElencoVers) {
	this.elvElencoVers = elvElencoVers;
    }

    @Column(name = "ID_SUB_STRUT")
    public Long getIdSubStrut() {
	return idSubStrut;
    }

    public void setIdSubStrut(Long idSubStrut) {
	this.idSubStrut = idSubStrut;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public AroXmlUnitaDocObjectStorage getAroXmlUnitaDocObjectStorage() {
	return aroXmlUnitaDocObjectStorage;
    }

    public void setAroXmlUnitaDocObjectStorage(
	    AroXmlUnitaDocObjectStorage aroXmlUnitaDocObjectStorage) {
	this.aroXmlUnitaDocObjectStorage = aroXmlUnitaDocObjectStorage;
    }

    // bi-directional one-to-one association to AroVersIniUnitaDoc
    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroVersIniUnitaDoc> getAroVersIniUnitaDocs() {
	return this.aroVersIniUnitaDocs;
    }

    public void setAroVersIniUnitaDocs(List<AroVersIniUnitaDoc> aroVersIniUnitaDocs) {
	this.aroVersIniUnitaDocs = aroVersIniUnitaDocs;
    }
}
