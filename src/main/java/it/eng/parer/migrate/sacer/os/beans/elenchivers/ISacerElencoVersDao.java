package it.eng.parer.migrate.sacer.os.beans.elenchivers;

import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvElencoVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.ElvFileElencoVers;

public interface ISacerElencoVersDao {
    /**
     *
     * @param filter filtro applicato
     *
     * @return lista pk
     */
    Stream<Long> findIdsElvElencoVers(FilterDto filter);

    void saveObjectStorageLinkFileElencoVersUd(String tenant, String bucket, String key,
	    long idFileElencoVers, Long idBackend) throws AppMigrateOsS3Exception;

    /**
     *
     * @param idElencoVers id elenco versamento
     *
     * @return lista resultset con sessioni identificate
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     *
     */
    ElvFileElencoVers findFileElencoVersByIdFileElencoVers(Long idFileElencoVers)
	    throws AppMigrateOsS3Exception;

    ElvElencoVers findElencoVersById(Long idElencoVers) throws AppMigrateOsS3Exception;

    void deleteBlFileElencoVers(Long idFileElencoVers) throws AppMigrateOsDeleteSrcException;
}
