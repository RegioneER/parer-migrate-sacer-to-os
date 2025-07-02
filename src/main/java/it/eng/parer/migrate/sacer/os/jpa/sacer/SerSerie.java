package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

/**
 * The persistent class for the SER_SERIE database table.
 */
@Entity
@Table(name = "SER_SERIE")

public class SerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSerie;

    private String cdCompositoSerie;

    public SerSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_SERIE")
    public Long getIdSerie() {
	return this.idSerie;
    }

    public void setIdSerie(Long idSerie) {
	this.idSerie = idSerie;
    }

    @Column(name = "CD_COMPOSITO_SERIE")
    public String getCdCompositoSerie() {
	return this.cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
	this.cdCompositoSerie = cdCompositoSerie;
    }

}
