package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_INDICE_AIP_UD")
@NamedQuery(name = "AroIndiceAipUd.findAll", query = "SELECT a FROM AroIndiceAipUd a")
public class AroIndiceAipUd {

    private static final long serialVersionUID = 1L;

    private Long idIndiceAip;

    private AroUnitaDoc aroUnitaDoc;

    private AroVerIndiceAipUd aroVerIndiceAipLast;

    public AroIndiceAipUd() {
	// hibernate
    }

    @Id
    @Column(name = "ID_INDICE_AIP")
    public Long getIdIndiceAip() {
	return this.idIndiceAip;
    }

    public void setIdIndiceAip(Long idIndiceAip) {
	this.idIndiceAip = idIndiceAip;
    }

    // bi-directional many-to-one association to AroUnitaDoc
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_INDICE_AIP_LAST")
    public AroVerIndiceAipUd getAroVerIndiceAipLast() {
	return this.aroVerIndiceAipLast;
    }

    public void setAroVerIndiceAipLast(AroVerIndiceAipUd aroVerIndiceAipLast) {
	this.aroVerIndiceAipLast = aroVerIndiceAipLast;
    }

}
