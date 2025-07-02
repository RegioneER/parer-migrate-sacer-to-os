package it.eng.parer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Dependent
@Transactional(Transactional.TxType.MANDATORY)
public class DatabaseInit {

    private static final String SCRIPT_PATH = "/h2/";

    @Inject
    EntityManager em;

    public void insertRequest() {
	exceInsertScript("request.sql");
    }

    public void insertRequestWithExistingUuid() {
	exceInsertScript("request2.sql");
    }

    public void insertRequestAllRunning() {
	exceInsertScript("request_all_running.sql");
    }

    public void insertFilter() {
	exceInsertScript("filter.sql");
    }

    public void insertObjectStorage() {
	insertRequest();
	insertFilter();
	exceInsertScript("os.sql");
    }

    private int exceInsertScript(String filename) {
	try {
	    final String sql = IOUtils.toString(
		    this.getClass().getResourceAsStream(SCRIPT_PATH + filename),
		    StandardCharsets.UTF_8);
	    Query query = em.createNativeQuery(sql);
	    return query.executeUpdate();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}
