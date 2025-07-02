package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DEC_CONFIG_OBJECT_STORAGE")
public class DecConfigObjectStorage implements Serializable {

    private static final long serialVersionUID = 33456789043235L;

    private Long idDecConfigObjectStorage;

    private DecBackend decBackend;

    private String nmConfigObjectStorage;

    private String dsValoreConfigObjectStorage;

    private String tiUsoConfigObjectStorage;

    private String dsDescrizioneConfigObjectStorage;

    public DecConfigObjectStorage() {
	super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEC_CONFIG_OBJECT_STORAGE")
    public Long getIdDecConfigObjectStorage() {
	return idDecConfigObjectStorage;
    }

    public void setIdDecConfigObjectStorage(Long idDecConfigObjectStorage) {
	this.idDecConfigObjectStorage = idDecConfigObjectStorage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEC_BACKEND")
    public DecBackend getDecBackend() {
	return decBackend;
    }

    public void setDecBackend(DecBackend decBackend) {
	this.decBackend = decBackend;
    }

    /**
     * Attualmente questa colonna accetta solamente questi valori:
     * <ul>
     * <li>BUCKET</li>
     * <li>ACCESS_KEY_ID_SYS_PROP</li>
     * <li>SECRET_KEY_SYS_PROP</li>
     * </ul>
     *
     * @return a cosa si riferiesce questa configurazione dell'OS
     */
    @Column(name = "NM_CONFIG_OBJECT_STORAGE")
    public String getNmConfigObjectStorage() {
	return nmConfigObjectStorage;
    }

    public void setNmConfigObjectStorage(String nmConfigObjectStorage) {
	this.nmConfigObjectStorage = nmConfigObjectStorage;
    }

    @Column(name = "DS_VALORE_CONFIG_OBJECT_STORAGE")
    public String getDsValoreConfigObjectStorage() {
	return dsValoreConfigObjectStorage;
    }

    public void setDsValoreConfigObjectStorage(String dsValoreConfigObjectStorage) {
	this.dsValoreConfigObjectStorage = dsValoreConfigObjectStorage;
    }

    /**
     * Attualmente questa colonna accetta solamente questi valori:
     * <ul>
     * <li>STAGING</li>
     * <li>COMPONENTI</li>
     * <li>SIP</li>
     * <li>AIP</li>
     * </ul>
     *
     * @return di fatto il nome del bucket
     */
    @Column(name = "TI_USO_CONFIG_OBJECT_STORAGE")
    public String getTiUsoConfigObjectStorage() {
	return tiUsoConfigObjectStorage;
    }

    public void setTiUsoConfigObjectStorage(String tiUsoConfigObjectStorage) {
	this.tiUsoConfigObjectStorage = tiUsoConfigObjectStorage;
    }

    @Column(name = "DS_DESCRIZIONE_CONFIG_OBJECT_STORAGE")
    public String getDsDescrizioneConfigObjectStorage() {
	return dsDescrizioneConfigObjectStorage;
    }

    public void setDsDescrizioneConfigObjectStorage(String dsDescrizioneConfigObjectStorage) {
	this.dsDescrizioneConfigObjectStorage = dsDescrizioneConfigObjectStorage;
    }

}
