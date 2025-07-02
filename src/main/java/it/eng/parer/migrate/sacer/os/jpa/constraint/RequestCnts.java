package it.eng.parer.migrate.sacer.os.jpa.constraint;

public final class RequestCnts {

    private RequestCnts() {
    }

    public enum State {
	REGISTERED, WAITING, STARTED, IN_PROGRESS, FINISHED, ERROR
    }

    public enum Type {
	SIP, COMP, AIP, SIPUPDUD, ELENCO_INDICI_AIP, AIP_SERIE, INDICE_ELENCO_VERS,
	UPD_DATI_SPEC_INI, DATI_SPEC_VERS
    }
}
