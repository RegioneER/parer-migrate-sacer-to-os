package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_XML_UNITA_DOC_OBJECT_STORAGE")
public class AroXmlUnitaDocObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idUnitaDoc;

    public AroXmlUnitaDocObjectStorage() {
	super();
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public Long getIdUnitaDoc() {
	return idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

}
