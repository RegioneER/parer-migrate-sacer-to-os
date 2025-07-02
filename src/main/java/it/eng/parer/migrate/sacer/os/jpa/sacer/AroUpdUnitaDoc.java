package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "ARO_UPD_UNITA_DOC")
public class AroUpdUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdUnitaDoc;

    private Long idStrut;

    private OrgStrut orgStrut;

    private AroUnitaDoc aroUnitaDoc;

    private BigDecimal aaKeyUnitaDoc;

    private BigDecimal pgUpdUnitaDoc;

    private List<AroUpdDatiSpecUnitaDoc> aroUpdDatiSpecUnitaDocs = new ArrayList<>();

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    public Long getIdUpdUnitaDoc() {
	return idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(Long idUpdUnitaDoc) {
	this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @OneToOne
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "PG_UPD_UNITA_DOC")
    public BigDecimal getPgUpdUnitaDoc() {
	return pgUpdUnitaDoc;
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
	this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    // bi-directional many-to-one association to AroUpdDatiSpecUnitaDoc
    @OneToMany(mappedBy = "aroUpdUnitaDoc")
    @XmlTransient
    public List<AroUpdDatiSpecUnitaDoc> getAroUpdDatiSpecUnitaDocs() {
	return this.aroUpdDatiSpecUnitaDocs;
    }

    public void setAroUpdDatiSpecUnitaDocs(List<AroUpdDatiSpecUnitaDoc> aroUpdDatiSpecUnitaDocs) {
	this.aroUpdDatiSpecUnitaDocs = aroUpdDatiSpecUnitaDocs;
    }

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT", insertable = false, updatable = false)
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }
}
