package it.eng.parer.migrate.sacer.os.beans.aip;

import java.util.List;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsAipService {
    /**
     *
     * Ricerca le componenti di unità documentarie con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationAipFromRequest(Long idRequest);

    /**
     * Registra richiesta di migrazione AIP
     *
     * @param osAipRequests lista richiesta/e di migrazione
     *
     * @return lista richiesta/e aggiornate
     */
    List<RequestDto> registerMigrationAipRequest(List<MigrateRequest> osAipRequests);
}