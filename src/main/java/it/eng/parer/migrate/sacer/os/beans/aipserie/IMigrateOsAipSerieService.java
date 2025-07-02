package it.eng.parer.migrate.sacer.os.beans.aipserie;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

import java.util.List;

public interface IMigrateOsAipSerieService {
    /**
     *
     * Ricerca le componenti di unità documentarie con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationAipSerieFromRequest(Long idRequest);

    /**
     * Registra richiesta di migrazione AIP
     *
     * @param osAipRequests lista richiesta/e di migrazione
     *
     * @return lista richiesta/e aggiornate
     */
    List<RequestDto> registerMigrationAipSerieRequest(List<MigrateRequest> osAipRequests);
}
