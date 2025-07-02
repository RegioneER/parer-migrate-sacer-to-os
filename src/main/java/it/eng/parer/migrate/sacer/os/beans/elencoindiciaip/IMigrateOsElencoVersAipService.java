package it.eng.parer.migrate.sacer.os.beans.elencoindiciaip;

import java.util.List;

import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;

public interface IMigrateOsElencoVersAipService {
    /**
     *
     * Ricerca le sessioni di versamento con il filtro applicato
     *
     * @param idRequest id della richiesta
     *
     */
    void processMigrationIndiceAipElencoVersUdFromRequest(Long idRequest);

    /**
     * Registra il tipo di richiesta SIP
     *
     * @param osSipRequests richiesta/e di migrazione
     *
     * @return lista delle richieste registrate
     */
    List<RequestDto> registerMigrationIndiceAipElencoVersUdRequest(
	    List<MigrateRequest> osSipRequests);
}
