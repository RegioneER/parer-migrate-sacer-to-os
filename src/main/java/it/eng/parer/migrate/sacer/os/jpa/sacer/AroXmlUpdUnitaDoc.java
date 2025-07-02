package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_XML_UPD_UNITA_DOC")
public class AroXmlUpdUnitaDoc {
    private static final long serialVersionUID = 1L;

    private Long idXmlUpdUnitaDoc;

    private AroUpdUnitaDoc aroUpdUnitaDoc;

    private String tiXmlUpdUnitaDoc;

    private String blXml;

    @Id
    @Column(name = "ID_XML_UPD_UNITA_DOC")
    public Long getIdXmlUpdUnitaDoc() {
	return idXmlUpdUnitaDoc;
    }

    public void setIdXmlUpdUnitaDoc(Long idXmlUpdUnitaDoc) {
	this.idXmlUpdUnitaDoc = idXmlUpdUnitaDoc;
    }

    @OneToOne
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
	return aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
	this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }

    @Column(name = "TI_XML_UPD_UNITA_DOC")
    public String getTiXmlUpdUnitaDoc() {
	return tiXmlUpdUnitaDoc;
    }

    public void setTiXmlUpdUnitaDoc(String tiXmlUpdUnitaDoc) {
	this.tiXmlUpdUnitaDoc = tiXmlUpdUnitaDoc;
    }

    @Lob()
    @Column(name = "BL_XML")
    public String getBlXml() {
	return blXml;
    }

    public void setBlXml(String blXml) {
	this.blXml = blXml;
    }
}
