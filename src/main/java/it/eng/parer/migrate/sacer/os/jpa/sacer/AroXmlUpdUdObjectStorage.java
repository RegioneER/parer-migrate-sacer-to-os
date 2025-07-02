package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_XML_UPD_UD_OBJECT_STORAGE")
public class AroXmlUpdUdObjectStorage extends AroObjectStorage {
    private static final long serialVersionUID = 1L;

    private Long idUpdUnitaDoc;

    private Long idStrut;

    public AroXmlUpdUdObjectStorage() {
	super();
    }

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    public Long getIdUpdUnitaDoc() {
	return idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(Long idUpdUnitaDoc) {
	this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }
}
