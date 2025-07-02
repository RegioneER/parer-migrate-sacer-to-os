package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_FILE_VER_INDICE_AIP_UD")
@NamedQuery(name = "AroFileVerIndiceAipUd.findAll", query = "SELECT a FROM AroFileVerIndiceAipUd a")
public class AroFileVerIndiceAipUd {

    private static final long serialVersionUID = 1L;

    private Long idFileVerIndiceAipUd;

    private AroVerIndiceAipUd aroVerIndiceAipUd;

    private String blFileVerIndiceAip;

    private Long idStrut;

    private AroVerIndiceAipUdObjectStorage aroVerIndiceAipUdObjectStorage;

    public AroFileVerIndiceAipUd() {
	// hibernate
    }

    @Id
    @Column(name = "ID_FILE_VER_INDICE_AIP")
    public Long getIdFileVerIndiceAipUd() {
	return this.idFileVerIndiceAipUd;
    }

    public void setIdFileVerIndiceAipUd(Long idFileVerIndiceAipUd) {
	this.idFileVerIndiceAipUd = idFileVerIndiceAipUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_INDICE_AIP")
    public AroVerIndiceAipUd getAroVerIndiceAipUd() {
	return this.aroVerIndiceAipUd;
    }

    public void setAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
	this.aroVerIndiceAipUd = aroVerIndiceAipUd;
    }

    @Lob()
    @Column(name = "BL_FILE_VER_INDICE_AIP")
    public String getBlFileVerIndiceAip() {
	return this.blFileVerIndiceAip;
    }

    public void setBlFileVerIndiceAip(String blFileVerIndiceAip) {
	this.blFileVerIndiceAip = blFileVerIndiceAip;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public AroVerIndiceAipUdObjectStorage getAroVerIndiceAipUdObjectStorage() {
	return aroVerIndiceAipUdObjectStorage;
    }

    public void setAroVerIndiceAipUdObjectStorage(
	    AroVerIndiceAipUdObjectStorage aroVerIndiceAipUdObjectStorage) {
	this.aroVerIndiceAipUdObjectStorage = aroVerIndiceAipUdObjectStorage;
    }
}
