package it.eng.parer.migrate.sacer.os.beans.datispecvers;

import java.util.List;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsDatiSpecVersService {
    /**
     *
     * Ricerca le componenti di unità documentarie con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationDatiSpecVersFromRequest(Long idRequest);

    /**
     * Registra richiesta di migrazione UPD_DATI_SPEC_INI
     *
     * @param osDatiSpecVersRequests lista richiesta/e di migrazione
     *
     * @return lista richiesta/e aggiornate
     */
    List<RequestDto> registerMigrationDatiSpecVersRequest(
	    List<MigrateRequest> osDatiSpecVersRequests);
}
