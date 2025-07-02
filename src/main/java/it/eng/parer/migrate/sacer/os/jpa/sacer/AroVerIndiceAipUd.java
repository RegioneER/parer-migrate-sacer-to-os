package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_VER_INDICE_AIP_UD")
@NamedQuery(name = "AroVerIndiceAipUd.findAll", query = "SELECT a FROM AroVerIndiceAipUd a")
public class AroVerIndiceAipUd {

    private static final long serialVersionUID = 1L;

    private Long idVerIndiceAip;

    private AroIndiceAipUd aroIndiceAipUd;

    private BigDecimal pgVerIndiceAip;

    private String cdVerIndiceAip;

    public AroVerIndiceAipUd() {
	// hibernate
    }

    @Id
    @Column(name = "ID_VER_INDICE_AIP")
    public Long getIdVerIndiceAip() {
	return idVerIndiceAip;
    }

    public void setIdVerIndiceAip(Long idVerIndiceAip) {
	this.idVerIndiceAip = idVerIndiceAip;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INDICE_AIP")
    public AroIndiceAipUd getAroIndiceAipUd() {
	return aroIndiceAipUd;
    }

    public void setAroIndiceAipUd(AroIndiceAipUd aroIndiceAipUd) {
	this.aroIndiceAipUd = aroIndiceAipUd;
    }

    @Column(name = "PG_VER_INDICE_AIP")
    public BigDecimal getPgVerIndiceAip() {
	return pgVerIndiceAip;
    }

    public void setPgVerIndiceAip(BigDecimal pgVerIndiceAip) {
	this.pgVerIndiceAip = pgVerIndiceAip;
    }

    @Column(name = "CD_VER_INDICE_AIP")
    public String getCdVerIndiceAip() {
	return cdVerIndiceAip;
    }

    public void setCdVerIndiceAip(String cdVerIndiceAip) {
	this.cdVerIndiceAip = cdVerIndiceAip;
    }

}
