package it.eng.parer.migrate.sacer.os.jpa.sacer;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARO_COMP_DOC_DA_MIGRARE_OS")
public class AroCompDocDaMigrareOs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCompDoc;

    private Long idContenComp;

    private BigDecimal idStrut;

    public AroCompDocDaMigrareOs() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public Long getIdCompDoc() {
	return this.idCompDoc;
    }

    public void setIdCompDoc(Long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_CONTEN_COMP")
    public Long getIdContenComp() {
	return this.idContenComp;
    }

    public void setIdContenComp(Long idContenComp) {
	this.idContenComp = idContenComp;
    }

}