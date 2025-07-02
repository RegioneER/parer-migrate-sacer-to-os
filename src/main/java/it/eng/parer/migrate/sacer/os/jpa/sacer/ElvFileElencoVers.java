package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.sql.Blob;

import it.eng.parer.migrate.sacer.os.jpa.sacer.constraint.ElvFileElencoVersCnts.TiFileElencoVers;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ELV_FILE_ELENCO_VERS")
public class ElvFileElencoVers {
    private static final long serialVersionUID = 1L;

    public ElvFileElencoVers() {/* Hibernate */
    }

    private Long idFileElencoVers;

    private ElvElencoVers elvElencoVers;

    private TiFileElencoVers tiFileElencoVers;

    private Blob blFileElencoVers;

    private Long idStrut;

    @Id
    @Column(name = "ID_FILE_ELENCO_VERS")
    public Long getIdFileElencoVers() {
	return idFileElencoVers;
    }

    public void setIdFileElencoVers(Long idFileElencoVers) {
	this.idFileElencoVers = idFileElencoVers;
    }

    @OneToOne
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVers getElvElencoVers() {
	return elvElencoVers;
    }

    public void setElvElencoVers(ElvElencoVers elvElencoVers) {
	this.elvElencoVers = elvElencoVers;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_FILE_ELENCO_VERS")
    public TiFileElencoVers getTiFileElencoVers() {
	return tiFileElencoVers;
    }

    public void setTiFileElencoVers(TiFileElencoVers tiFileElencoVers) {
	this.tiFileElencoVers = tiFileElencoVers;
    }

    @Column(name = "BL_FILE_ELENCO_VERS")
    public Blob getBlFileElencoVers() {
	return blFileElencoVers;
    }

    public void setBlFileElencoVers(Blob blFileElencoVers) {
	this.blFileElencoVers = blFileElencoVers;
    }

    @Column(name = "ID_STRUT", insertable = false, updatable = false)
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }
}
