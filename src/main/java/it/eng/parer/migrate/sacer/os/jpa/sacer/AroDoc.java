package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_DOC")
@NamedQuery(name = "AroDoc.findAll", query = "SELECT a FROM AroDoc a")
public class AroDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDoc;

    private BigDecimal idStrut;

    private AroUnitaDoc aroUnitaDoc;

    private BigDecimal pgDoc;

    private BigDecimal niOrdDoc;

    private List<AroStrutDoc> aroStrutDocs = new ArrayList<>();

    private AroXmlDocObjectStorage aroXmlDocObjectStorage;

    public AroDoc() {
	// hibernate
    }

    @Id
    @Column(name = "ID_DOC")
    public Long getIdDoc() {
	return this.idDoc;
    }

    public void setIdDoc(Long idDoc) {
	this.idDoc = idDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    // bi-directional many-to-one association to AroStrutDoc
    @OneToMany(mappedBy = "aroDoc")
    public List<AroStrutDoc> getAroStrutDocs() {
	return this.aroStrutDocs;
    }

    public void setAroStrutDocs(List<AroStrutDoc> aroStrutDocs) {
	this.aroStrutDocs = aroStrutDocs;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
	return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
	this.pgDoc = pgDoc;
    }

    @Column(name = "NI_ORD_DOC")
    public BigDecimal getNiOrdDoc() {
	return this.niOrdDoc;
    }

    public void setNiOrdDoc(BigDecimal niOrdDoc) {
	this.niOrdDoc = niOrdDoc;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public AroXmlDocObjectStorage getAroXmlDocObjectStorage() {
	return aroXmlDocObjectStorage;
    }

    public void setAroXmlDocObjectStorage(AroXmlDocObjectStorage aroXmlDocObjectStorage) {
	this.aroXmlDocObjectStorage = aroXmlDocObjectStorage;
    }

}
