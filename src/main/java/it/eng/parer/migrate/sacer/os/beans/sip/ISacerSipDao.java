/**
 *
 */
package it.eng.parer.migrate.sacer.os.beans.sip;

import java.util.List;
import java.util.stream.Stream;

import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsDeleteSrcException;
import it.eng.parer.migrate.sacer.os.exceptions.AppMigrateOsS3Exception;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsSessioneVers;
import it.eng.parer.migrate.sacer.os.jpa.sacer.VrsXmlDatiSessioneVers;

public interface ISacerSipDao {

    /**
     *
     * @param filter filtro applicato
     *
     * @return lista pk
     */
    Stream<Long> findIdsSessVersUdDocChiusaOk(FilterDto filter);

    void saveObjectStorageLinkSipUd(String tenant, String bucket, String key, Long idUnitaDoc,
	    Long idSacerBackend);

    void saveObjectStorageLinkSipDoc(String tenant, String bucket, String key, Long idDoc,
	    Long idSacerBackend);

    /**
     * Restituisce la lista di VrsXmlDatiSessioneVers data la sessione di versamento
     *
     * @param idSessioneVers id sessione versamento
     *
     * @return lista resultset con sessioni identificate
     *
     * @throws AppMigrateOsS3Exception eccezione generica
     *
     */
    List<VrsXmlDatiSessioneVers> findAllXmlDatiSessioneVersByIdSessVers(Long idSessioneVers)
	    throws AppMigrateOsS3Exception;

    VrsSessioneVers findVrsSessioneVersById(Long idSessioneVers) throws AppMigrateOsS3Exception;

    void deleteBlXmlOnVrsXmlDatiSessioneVers(List<Long> xmlDatiSessioneVersIds)
	    throws AppMigrateOsDeleteSrcException;

}