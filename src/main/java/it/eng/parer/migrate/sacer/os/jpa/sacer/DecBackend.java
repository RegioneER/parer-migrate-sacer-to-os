package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "DEC_BACKEND")
public class DecBackend implements Serializable {

    private static final long serialVersionUID = 37568565656845L;

    private Long idDecBackend;

    private String nmBackend;

    private String nmTipoBackend;

    private String dlBackendUri;

    private String dsDescrizioneBackend;

    private List<DecConfigObjectStorage> decConfigObjectStorages;

    public DecBackend() {
	super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEC_BACKEND")
    public Long getIdDecBackend() {
	return idDecBackend;
    }

    public void setIdDecBackend(Long idDecBackend) {
	this.idDecBackend = idDecBackend;
    }

    @Column(name = "NM_BACKEND")
    public String getNmBackend() {
	return nmBackend;
    }

    public void setNmBackend(String nmBackend) {
	this.nmBackend = nmBackend;
    }

    @Column(name = "NM_TIPO_BACKEND")
    public String getNmTipoBackend() {
	return nmTipoBackend;
    }

    public void setNmTipoBackend(String nmTipoBackend) {
	this.nmTipoBackend = nmTipoBackend;
    }

    @Column(name = "DL_BACKEND_URI")
    public String getDlBackendUri() {
	return dlBackendUri;
    }

    public void setDlBackendUri(String dlBackendUri) {
	this.dlBackendUri = dlBackendUri;
    }

    @Column(name = "DS_DESCRIZIONE_BACKEND")
    public String getDsDescrizioneBackend() {
	return dsDescrizioneBackend;
    }

    public void setDsDescrizioneBackend(String dsDescrizioneBackend) {
	this.dsDescrizioneBackend = dsDescrizioneBackend;
    }

    @OneToMany(mappedBy = "decBackend")
    public List<DecConfigObjectStorage> getDecConfigObjectStorages() {
	return decConfigObjectStorages;
    }

    public void setDecConfigObjectStorages(List<DecConfigObjectStorage> decConfigObjectStorages) {
	this.decConfigObjectStorages = decConfigObjectStorages;
    }

}
