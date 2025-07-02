package it.eng.parer.migrate.sacer.os.jpa.constraint;

public final class ObjectStorageCnts {

    private ObjectStorageCnts() {
    }

    public enum State {
	TO_MIGRATE, MIGRATED, MIGRATION_ERROR
    }

    public enum IntegrityType {
	MD5, SHA256, CRC32C
    }

    public enum ObjectType {
	VRS_SESSIONE_VERS, ARO_COMP_DOC, ARO_INDICE_AIP_UD, ARO_UPD_UNITA_DOC, ELENCO_INDICI_AIP,
	SER_FILE_VER_SERIE_AIP, INDICE_ELENCO_VERS, ARO_UPD_DATI_SPEC_UNITA_DOC,
	ARO_VERS_INI_DATI_SPEC
    }

}
