package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the SER_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_VER_SERIE")
public class SerVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVerSerie;

    private String cdVerSerie;

    private SerSerie serSerie;

    private List<SerFileVerSerie> serFileVerSeries = new ArrayList<>();

    public SerVerSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public Long getIdVerSerie() {
	return this.idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
	return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
	this.cdVerSerie = cdVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SERIE")
    public SerSerie getSerSerie() {
	return this.serSerie;
    }

    public void setSerSerie(SerSerie serSerie) {
	this.serSerie = serSerie;
    }

    @OneToMany(mappedBy = "serVerSerie")
    public List<SerFileVerSerie> getSerFileVerSeries() {
	return this.serFileVerSeries;
    }

    public void setSerFileVerSeries(List<SerFileVerSerie> serFileVerSeries) {
	this.serFileVerSeries = serFileVerSeries;
    }

    public SerFileVerSerie addSerFileVerSery(SerFileVerSerie serFileVerSery) {
	getSerFileVerSeries().add(serFileVerSery);
	serFileVerSery.setSerVerSerie(this);
	return serFileVerSery;
    }

    public SerFileVerSerie removeSerFileVerSery(SerFileVerSerie serFileVerSery) {
	getSerFileVerSeries().remove(serFileVerSery);
	serFileVerSery.setSerVerSerie(null);
	return serFileVerSery;
    }
}
