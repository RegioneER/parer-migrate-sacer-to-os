package it.eng.parer.migrate.sacer.os.jpa.sacer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_XML_DOC_OBJECT_STORAGE")
public class AroXmlDocObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idDoc;

    public AroXmlDocObjectStorage() {
	super();
    }

    @Id
    @Column(name = "ID_DOC")
    public Long getIdDoc() {
	return idDoc;
    }

    public void setIdDoc(Long idDoc) {
	this.idDoc = idDoc;
    }

}
