package it.eng.parer.migrate.sacer.os.beans.comp;

import java.util.List;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsCompService {

    /**
     *
     * Ricerca le componenti di unità documentarie con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationCompFromRequest(Long idRequest);

    /**
     * Registra richiesta di migrazione COMP
     *
     * @param osCompRequests lista richiesta/e di migrazione
     *
     * @return lista richiesta/e aggiornate
     */
    List<RequestDto> registerMigrationCompRequest(List<MigrateRequest> osCompRequests);

}
