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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_STRUT_DOC")
@NamedQuery(name = "AroStrutDoc.findAll", query = "SELECT a FROM AroStrutDoc a")
public class AroStrutDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStrutDoc;

    private BigDecimal idStrut;

    private List<AroCompDoc> aroCompDocs = new ArrayList<>();

    private AroDoc aroDoc;

    public AroStrutDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_STRUT_DOC")
    public Long getIdStrutDoc() {
	return this.idStrutDoc;
    }

    public void setIdStrutDoc(Long idStrutDoc) {
	this.idStrutDoc = idStrutDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    // bi-directional many-to-one association to AroCompDoc
    @OneToMany(mappedBy = "aroStrutDoc")
    public List<AroCompDoc> getAroCompDocs() {
	return this.aroCompDocs;
    }

    public void setAroCompDocs(List<AroCompDoc> aroCompDocs) {
	this.aroCompDocs = aroCompDocs;
    }

    // bi-directional many-to-one association to AroDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOC")
    public AroDoc getAroDoc() {
	return this.aroDoc;
    }

    public void setAroDoc(AroDoc aroDoc) {
	this.aroDoc = aroDoc;
    }
}
