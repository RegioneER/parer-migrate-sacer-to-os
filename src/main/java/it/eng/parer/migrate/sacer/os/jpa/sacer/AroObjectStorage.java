package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class AroObjectStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDecBackend;

    private String nmTenant;

    private String nmBucket;

    private String cdKeyFile;

    public AroObjectStorage() {
	super();
    }

    @Column(name = "ID_DEC_BACKEND")
    public Long getIdDecBackend() {
	return idDecBackend;
    }

    public void setIdDecBackend(Long idDecBackend) {
	this.idDecBackend = idDecBackend;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
	return nmTenant;
    }

    public void setNmTenant(String nmTenant) {
	this.nmTenant = nmTenant;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
	return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
	this.nmBucket = nmBucket;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
	return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
	this.cdKeyFile = cdKeyFile;
    }

}
