package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_CONTENUTO_COMP")
public class AroContenutoComp implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idContenComp;

    private BigDecimal aaKeyUnitaDoc;

    private transient Blob blContenComp;

    private BigDecimal idStrut;

    private AroCompDoc aroCompDoc;

    public AroContenutoComp() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_CONTEN_COMP")
    public Long getIdContenComp() {
	return this.idContenComp;
    }

    public void setIdContenComp(Long idContenComp) {
	this.idContenComp = idContenComp;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob()
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "BL_CONTEN_COMP")
    public Blob getBlContenComp() {
	return this.blContenComp;
    }

    public void setBlContenComp(Blob blContenComp) {
	this.blContenComp = blContenComp;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    // bi-directional many-to-one association to AroCompDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMP_STRUT_DOC")
    public AroCompDoc getAroCompDoc() {
	return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
	this.aroCompDoc = aroCompDoc;
    }
}