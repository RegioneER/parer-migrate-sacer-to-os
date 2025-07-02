/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package it.eng.parer.migrate.sacer.os.base.model;

import java.time.LocalDate;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.eng.parer.migrate.sacer.os.base.validator.MigrateOsRequestValidator.ValidOsMigrationReq;
import jakarta.validation.constraints.Digits;

/*
 * Wrapper multipart-form con dati inviati da client. Presenti meccanismi di validazione da
 * javax.validation che verificano la presenza o meno dei campi all'interno del multipart scambiato.
 *
 * I campi presenti all'interno del multipart rappresentano il filtro di ricerca, a partire dalla
 * sessioni di versamento nelle quali ricercare successivamente gli XML che saranno, prima
 * "impacchettati" all'interno di un file ZIP di cui verrà calcolato lo SHA-256, dopodiché sarà
 * trasferito su Object Storage, di cui verrà validato lo SHA-256 restituito al termine del
 * trasferimento, infine il processo si concluderà con l'aggiornamento della base dati (come da
 * specifica) e dall'opzionale cancellazione.
 *
 * Campi ricercabili, vedi VRS_SESSIONE_VERS :
 *
 * ID_DOC ID_SESSIONE_VERS ID_STRUT e DT_APERTURA (sarà necessario limitare il numero di record
 * ottenuti per evitare operazioni troppo massive -> RAW LIMITS) ID_UNITA_DOC
 *
 *
 */
@JsonInclude(Include.NON_NULL)
@ValidOsMigrationReq(message = "Filtro indicato non corretto, valorizzare almeno un elemento tra idstrut / dtapertura o dtaperturayy con id_strut / idunitadoc (solo dev mode) / iddoc (solo dev mode)  / idsessionvers (solo dev mode) / idcomp (solo dev mode) / idverindiceaip (solo dev mode)")
public class MigrateRequest {

    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idunitadoc = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long iddoc = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idsessionvers = null;
    @Schema(required = false, nullable = true, defaultValue = "null")
    public Long idstrut = null;
    @Schema(required = false, nullable = true, defaultValue = "null")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate dtapertura = null;
    @Schema(required = false, nullable = true, defaultValue = "null")
    @Digits(integer = 4, fraction = 0, message = "Anno non valido")
    public Integer dtaperturayy = null;
    @Schema(required = false, nullable = true, defaultValue = "null")
    public Long rowlimit = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idcomp = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idelencovers = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idverindiceaip = null;
    @Schema(required = false, nullable = true, defaultValue = "null", description = "ENABLE ONLY on dev and test mode", title = "ENABLE ONLY on dev and test mode")
    public Long idverserie = null;
    @Schema(required = false, nullable = false, defaultValue = "true")
    public Boolean deletesrc = Boolean.TRUE;

    public MigrateRequest() {
	super();
    }

    public MigrateRequest(Long idunitadoc, Long iddoc, Long idsessionvers, Long idcomp,
	    Long idverindiceaip, Long idverserie, Long idstrut, Long idElencoVers,
	    LocalDate dtapertura, Integer dtaperturayy, Long rowlimit, Boolean deleteSrc) {
	super();
	this.idunitadoc = idunitadoc;
	this.iddoc = iddoc;
	this.idsessionvers = idsessionvers;
	this.idcomp = idcomp;
	this.idverindiceaip = idverindiceaip;
	this.idstrut = idstrut;
	this.dtapertura = dtapertura;
	this.dtaperturayy = dtaperturayy;
	this.rowlimit = rowlimit;
	this.deletesrc = deleteSrc;
	this.idverserie = idverserie;
	this.idelencovers = idElencoVers;
    }

    @Override
    public int hashCode() {
	return Objects.hash(deletesrc, dtapertura, dtaperturayy, idcomp, iddoc, idelencovers,
		idsessionvers, idstrut, idunitadoc, idverindiceaip, idverserie, rowlimit);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	MigrateRequest other = (MigrateRequest) obj;
	return Objects.equals(deletesrc, other.deletesrc)
		&& Objects.equals(dtapertura, other.dtapertura)
		&& Objects.equals(dtaperturayy, other.dtaperturayy)
		&& Objects.equals(idcomp, other.idcomp) && Objects.equals(iddoc, other.iddoc)
		&& Objects.equals(idelencovers, other.idelencovers)
		&& Objects.equals(idsessionvers, other.idsessionvers)
		&& Objects.equals(idstrut, other.idstrut)
		&& Objects.equals(idunitadoc, other.idunitadoc)
		&& Objects.equals(idverindiceaip, other.idverindiceaip)
		&& Objects.equals(idverserie, other.idverserie)
		&& Objects.equals(rowlimit, other.rowlimit);
    }

}
