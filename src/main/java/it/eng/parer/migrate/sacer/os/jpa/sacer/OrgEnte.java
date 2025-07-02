package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORG_ENTE")
public class OrgEnte {
    private static final long serialVersionUID = 1L;

    public OrgEnte() {/* Hibernate */
    }

    private Long idEnte;

    private String nmEnte;

    private OrgAmbiente orgAmbiente;

    @Id
    @Column(name = "ID_ENTE", insertable = false, updatable = false)
    public Long getIdEnte() {
	return idEnte;
    }

    public void setIdEnte(Long idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @OneToOne
    @JoinColumn(name = "ID_AMBIENTE")
    public OrgAmbiente getOrgAmbiente() {
	return orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
	this.orgAmbiente = orgAmbiente;
    }
}
