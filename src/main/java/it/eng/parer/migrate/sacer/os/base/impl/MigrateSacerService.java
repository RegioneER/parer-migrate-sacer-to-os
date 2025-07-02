package it.eng.parer.migrate.sacer.os.base.impl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.IMigrateSacerDao;
import it.eng.parer.migrate.sacer.os.base.IMigrateSacerService;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateSacerService implements IMigrateSacerService {

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @Inject
    IMigrateSacerDao sacerDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppGenericRuntimeException.class })
    public Long getIdDeckBackeEnd() {
	return sacerDao.findDecBakendByName(nmBackend).getIdDecBackend();
    }

}
