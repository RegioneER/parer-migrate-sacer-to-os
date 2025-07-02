package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORG_STRUT")
public class OrgStrut implements Serializable {

    private static final long serialVersionUID = 1L;

    public OrgStrut() {/* Hibernate */
    }

    private Long idStrut;

    private String nmStrut;

    private OrgEnte orgEnte;

    @Id
    @Column(name = "ID_STRUT", insertable = false, updatable = false)
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @OneToOne
    @JoinColumn(name = "ID_ENTE")
    public OrgEnte getOrgEnte() {
	return orgEnte;
    }

    public void setOrgEnte(OrgEnte orgEnte) {
	this.orgEnte = orgEnte;
    }

}
