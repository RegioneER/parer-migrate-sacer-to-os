/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.migrate.sacer.os.base.utils;

public class Costants {

    // Tipo XML (metadati)
    public static final String INDICE_FILE = "INDICE_FILE";
    public static final String RICHIESTA = "RICHIESTA";
    public static final String RISPOSTA = "RISPOSTA";
    public static final String RAPP_VERS = "RAPP_VERS";

    // tipo documento (XML)
    public static final String TI_XML_SESSIONE_VERS = "XML_DOC";

    // S3 keys
    public static final String S3_KEY_PREFIX_BYDATE_FMT = "yyyyMMdd/HH/mm";

    // ente/struttura
    public static final String S3_KEY_VERSATORE_FMT = "{0}/{1}";
    // registro-anno-chiave
    public static final String S3_KEY_UD_FMT = "{0}-{1}-{2}";
    public static final String S3_KEY_AIP_UD_FMT = "{0}-{1}-{2}_IndiceAIPUD_{3}";
    // doc urn
    public static final String S3_KEY_DOC_PREFIX = "DOC";
    public static final String S3_KEY_DOC_FMT = "{0}{1}";
    public static final String S3_KEY_DOC_COMP_FMT = "{0}-{1}";
    // indice elenco vers
    public static final String S3_KEY_VERSATORE_AIP_SERIE_FMT = "{0}/{1}/{2}";
    public static final String S3_KEY_COMP_FMT = "{0}/{1}/{2}";
    public static final String S3_KEY_AIP_FMT = "{0}/{1}_{2}";
    public static final String S3_KEY_ELENCO_VERS_FMT = "{0}/{1}";

    public static final String S3_KEY_PAD5DIGITS_FMT = "%05d";
    // final URN
    public static final String S3_KEY_URN_UD_FMT = "{0}/{1}_SIPUD";
    public static final String S3_KEY_URN_DOC_FMT = "{0}/{1}_SIPDOC";
    public static final String S3_KEY_URN_AIP_SERIE_FMT = "{0}_IndiceAIPSE_{1}_NonFirmato";
    public static final String S3_KEY_URN_INDICE_AIP_SERIE_UD_MARCA_FMT = "{0}_IndiceAIPSE_{1}_IndiceMarca";
    public static final String S3_KEY_URN_INDICE_AIP_SERIE_UD_FIR_FMT = "{0}_IndiceAIPSE_{1}";
    public static final String S3_KEY_URN_UPD_FMT = "{0}/{1}_AGGMD{2}";
    public static final String S3_KEY_URN_FIRMA_ELENCO_INDICI_AIP_FMT = "ElencoIndiciAIPUD_{0}_{1}_Indice";
    public static final String S3_KEY_URN_MARCA_FIRMA_ELENCO_INDICI_AIP_FMT = "ElencoIndiciAIPUD_{0}_{1}_IndiceMarca";
    public static final String S3_KEY_URN_INDICE_ELENCO_VERS_FMT = "ElencoVersUD_{0}_{1}_Indice";
    public static final String S3_KEY_URN_INDICE_FIRMATO_ELENCO_VERS_FMT = "ElencoVersUD_{0}_{1}_IndiceFirmato";
    public static final String S3_KEY_URN_MARCA_INDICE_ELENCO_VERS_FMT = "ElencoVersUD_{0}_{1}_IndiceMarca";
    public static final String S3_KEY_URN_FIRMA_ELENCO_VERS_FMT = "ElencoVersUD_{0}_{1}_Firma";
    public static final String S3_KEY_URN_MARCA_FIRMA_ELENCO_VERS_FMT = "ElencoVersUD_{0}_{1}_MarcaFirma";

    public enum TipoSessione {

	VERSAMENTO, AGGIUNGI_DOCUMENTO
    }

    public enum TiStatoSesioneVers {
	CHIUSA_OK
    }

    public enum ChecksumAlghoritm {
	MD5("MD5"), SHA256("SHA-256"), CRC32C("CRC32C");

	private String instance;

	private ChecksumAlghoritm(String instance) {
	    this.instance = instance;
	}

	public String getInstance() {
	    return instance;
	}

	public static ChecksumAlghoritm evalute(String instance) {
	    for (ChecksumAlghoritm v : values()) {
		if (v.getInstance().equalsIgnoreCase(instance)) {
		    return v;
		}
	    }
	    return MD5; // default
	}
    }

}
