package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORG_AMBIENTE")
public class OrgAmbiente {
    private static final long serialVersionUID = 1L;

    public OrgAmbiente() {/* Hibernate */
    }

    private Long idAmbiente;

    private String nmAmbiente;

    @Id
    @Column(name = "ID_AMBIENTE", insertable = false, updatable = false)
    public Long getIdAmbiente() {
	return idAmbiente;
    }

    public void setIdAmbiente(Long idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }
}
