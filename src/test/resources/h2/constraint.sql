ALTER TABLE REQUESTS DROP CONSTRAINT "TI_MIGRATION_TYPE";

ALTER TABLE REQUESTS ADD CONSTRAINT "TI_MIGRATION_TYPE" CHECK ( MIGRATION_TYPE IN ('SIP', 'COMP', 'AIP', 'SIPUPDUD', 'ELENCO_INDICI_AIP', 'AIP_SERIE', 'INDICE_ELENCO_VERS'));

