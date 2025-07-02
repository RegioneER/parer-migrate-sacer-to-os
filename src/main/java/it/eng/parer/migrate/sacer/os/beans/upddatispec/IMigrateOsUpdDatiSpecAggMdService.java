package it.eng.parer.migrate.sacer.os.beans.upddatispec;

import java.util.List;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsUpdDatiSpecAggMdService {
    /**
     *
     * Ricerca le componenti di unità documentarie con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationUpdDatiSpecAggMdFromRequest(Long idRequest);

    /**
     * Registra richiesta di migrazione UPD_DATI_SPEC_INI
     *
     * @param osUpdDatiSpecAggMdRequests lista richiesta/e di migrazione
     *
     * @return lista richiesta/e aggiornate
     */
    List<RequestDto> registerMigrationUpdDatiSpecAggMdRequest(
	    List<MigrateRequest> osUpdDatiSpecAggMdRequests);
}