package it.eng.parer.migrate.sacer.os.jpa.sacer;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.migrate.sacer.os.jpa.sacer.sequence.NonMonotonicSequenceGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_COMP_OBJECT_STORAGE")
public class AroCompObjectStorage extends AroObjectStorage {

    private static final long serialVersionUID = 1L;

    private Long idCompObjectStorage;

    private Long idCompDoc;

    public AroCompObjectStorage() {
	super();
    }

    @Id
    @Column(name = "ID_COMP_OBJECT_STORAGE")
    @GenericGenerator(name = "SARO_COMP_OBJECT_STORAGE_ID_COMP_OBJECT_STORAGE_GENERATOR", type = NonMonotonicSequenceGenerator.class, parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_COMP_OBJECT_STORAGE"),
	    @Parameter(name = OptimizableGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_COMP_OBJECT_STORAGE_ID_COMP_OBJECT_STORAGE_GENERATOR")
    public Long getIdCompObjectStorage() {
	return this.idCompObjectStorage;
    }

    public void setIdCompObjectStorage(Long idCompObjectStorage) {
	this.idCompObjectStorage = idCompObjectStorage;
    }

    @Column(name = "ID_COMP_DOC")
    public Long getIdCompDoc() {
	return idCompDoc;
    }

    public void setIdCompDoc(Long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

}
