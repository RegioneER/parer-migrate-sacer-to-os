package it.eng.parer.migrate.sacer.os.base.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.jpa.entity.Filters;

/**
 * The persistent class for the DEC_TITOL database table.
 *
 */
@JsonInclude(Include.NON_NULL)
public class FilterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUnitadoc;
    private Long idDoc;
    private Long idSessioneVers;
    private Long idStrut;
    private Long idCompDoc;
    private Long idVerIndiceAip;
    private Long rowlimit;
    private LocalDate dtApertura;
    private Integer dtAperturaYY;
    private Long idVerSerie;
    private Long idElencoVers;

    public FilterDto() {
	super();
    }

    private FilterDto(Long idUnitadoc, Long idDoc, Long idSessioneVers, Long idCompDoc,
	    Long idStrut, Long idElencoVers, Long idVerIndiceAip, Long idVerSerie,
	    LocalDate dtApertura, Integer dtAperturaYY, Long rowlimit) {
	super();
	this.idUnitadoc = idUnitadoc;
	this.idDoc = idDoc;
	this.idSessioneVers = idSessioneVers;
	this.idCompDoc = idCompDoc;
	this.idStrut = idStrut;
	this.dtApertura = dtApertura;
	this.dtAperturaYY = dtAperturaYY;
	this.rowlimit = rowlimit;
	this.idElencoVers = idElencoVers;
	this.idVerIndiceAip = idVerIndiceAip;
	this.idVerSerie = idVerSerie;
    }

    public FilterDto(Filters filters) {
	this(filters.getIdUnitadoc(), filters.getIdDoc(), filters.getIdSessioneVers(),
		filters.getIdCompDoc(), filters.getIdStrut(), filters.getIdElencoVers(),
		filters.getIdVerIndiceAip(), filters.getIdVerSerie(), filters.getDtApertura(),
		filters.getDtAperturaYY(), filters.getRowlimit());
    }

    public FilterDto(MigrateRequest request) {
	this(request.idunitadoc, request.iddoc, request.idsessionvers, request.idcomp,
		request.idstrut, request.idelencovers, request.idverindiceaip, request.idverserie,
		request.dtapertura, request.dtaperturayy, request.rowlimit);
    }

    public Long getIdUnitadoc() {
	return idUnitadoc;
    }

    public void setIdUnitadoc(Long idUnitadoc) {
	this.idUnitadoc = idUnitadoc;
    }

    public Long getIdDoc() {
	return idDoc;
    }

    public void setIdDoc(Long idDoc) {
	this.idDoc = idDoc;
    }

    public Long getIdSessioneVers() {
	return idSessioneVers;
    }

    public void setIdSessioneVers(Long idSessioneVers) {
	this.idSessioneVers = idSessioneVers;
    }

    public Long getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(Long idStrut) {
	this.idStrut = idStrut;
    }

    public Long getRowlimit() {
	return rowlimit;
    }

    public void setRowlimit(Long rowlimit) {
	this.rowlimit = rowlimit;
    }

    public LocalDate getDtApertura() {
	return dtApertura;
    }

    public void setDtApertura(LocalDate dtApertura) {
	this.dtApertura = dtApertura;
    }

    public Integer getDtAperturaYY() {
	return dtAperturaYY;
    }

    public void setDtAperturaYY(Integer dtAperturaYY) {
	this.dtAperturaYY = dtAperturaYY;
    }

    public Long getIdCompDoc() {
	return idCompDoc;
    }

    public void setIdCompDoc(Long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    public Long getIdElencoVers() {
	return idElencoVers;
    }

    public void setIdElencoVers(Long idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    public Long getIdVerIndiceAip() {
	return idVerIndiceAip;
    }

    public void setIdVerIndiceAip(Long idVerIndiceAip) {
	this.idVerIndiceAip = idVerIndiceAip;
    }

    public Long getIdVerSerie() {
	return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

}