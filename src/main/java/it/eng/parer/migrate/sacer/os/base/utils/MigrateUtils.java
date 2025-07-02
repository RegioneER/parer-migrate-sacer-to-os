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

import static it.eng.parer.migrate.sacer.os.base.utils.Costants.S3_KEY_PREFIX_BYDATE_FMT;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import it.eng.parer.migrate.sacer.os.base.utils.Costants.ChecksumAlghoritm;

public class MigrateUtils {

    private static final int BUFFER_10M = 10 * 1024 * 1024; // 10 Mb

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static final FileAttribute<Set<PosixFilePermission>> POSIX_STD_ATTR = PosixFilePermissions
	    .asFileAttribute(PosixFilePermissions.fromString("rw-------"));

    private MigrateUtils() {
	throw new IllegalStateException("Utility class");
    }

    /*
     * Get the name of the host if null -> return empty
     */
    public static final Optional<String> getHostname() {
	try {
	    return Optional.of(InetAddress.getLocalHost().getHostName());
	} catch (UnknownHostException e) {
	    return Optional.empty();
	}
    }

    /**
     * Calcola il message digest MD5/SHA256/CRC£2C (base64 encoded) del file da inviare via S3
     *
     * Nota: questa scelta deriva dal modello supportato dal vendor
     * (https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html)
     *
     * @param tempFile          file temporaneo
     * @param checksumAlghoritm tipo di istanza utilizzato (in questo caso MD5)
     *
     * @return rappresentazione base64 del contenuto calcolato
     *
     * @throws NoSuchAlgorithmException eccezione generica
     * @throws IOException              eccezione generica
     */
    public static String calculateFileBase64(Path tempFile, ChecksumAlghoritm checksumAlghoritm)
	    throws NoSuchAlgorithmException, IOException {
	byte[] value = null;

	if (checksumAlghoritm.compareTo(ChecksumAlghoritm.CRC32C) == 0) {
	    value = calculateCRC32C(tempFile, BUFFER_10M);
	} else {
	    value = calculateDigest(tempFile, checksumAlghoritm.getInstance(), BUFFER_10M);
	}

	return Base64.getEncoder().encodeToString(value);
    }

    private static byte[] calculateDigest(Path tempFile, String intance, Integer size)
	    throws NoSuchAlgorithmException, IOException {
	byte[] buffer = new byte[size];
	int readed;
	MessageDigest digester = MessageDigest.getInstance(intance);
	try (InputStream is = Files.newInputStream(tempFile)) {
	    while ((readed = is.read(buffer)) != -1) {
		digester.update(buffer, 0, readed);
	    }
	}
	return digester.digest();
    }

    private static byte[] calculateCRC32C(Path tempFile, Integer size) throws IOException {
	byte[] buffer = new byte[size];
	int readed;
	CRC32CChecksum cRC32CChecksum = new CRC32CChecksum();
	try (InputStream is = Files.newInputStream(tempFile)) {
	    while ((readed = is.read(buffer)) != -1) {
		cRC32CChecksum.update(buffer, 0, readed);
	    }
	}
	return cRC32CChecksum.getValueAsBytes();
    }

    /**
     * Crea una chiave utilizzando i seguenti elementi separati dal carattere <code>/</code>:
     * <ul>
     * <li>data in formato anno mese giorno (per esempio <strong>20221124</strong>)</li>
     * <li>ora a due cifre (per esempio <strong>14</strong>)</li>
     * <li>minuto a due cifre (per esempio <strong>05</strong>)</li>
     * <li>UUID generato runtime <strong>28fd282d-fbe6-4528-bd28-2dfbe685286f</strong>) per ogni
     * oggetto caricato</li>
     * </ul>
     *
     * Esempio di chiave completa:
     * <code>20221124/14/05/urn/28fd282d-fbe6-4528-bd28-2dfbe685286f</code>
     *
     * @return chiave dell'oggetto
     */
    public static String createS3RandomKey(final String urn) {

	String when = DateTimeFormatter.ofPattern(S3_KEY_PREFIX_BYDATE_FMT)
		.withZone(ZoneId.systemDefault()).format(Instant.now());

	return when + "/" + urn + "/" + UUID.randomUUID().toString();
    }

    /**
     * Default S3 metadata
     *
     * @return {@link Map} con metadati
     */
    public static Map<String, String> defaultS3Metadata(final String instanceUUID) {
	Map<String, String> defaultMetadata = new HashMap<>();
	defaultMetadata.put("ingest-node", "migrate-os-" + instanceUUID);
	defaultMetadata.put("ingest-time",
		ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
	return defaultMetadata;
    }

    /*
     * Restituisce una stringa normalizzata secondo le regole cel codice UD normalizzato sostituendo
     * tutti i caratteri accentati con i corrispondenti non accentati e ammettendo solo lettere,
     * numeri, '.', '-' e '_'. Tutto il resto viene convertito in '_'.
     */
    public static String normalizingKey(String value) {
	return Normalizer.normalize(value, Normalizer.Form.NFD).replace(" ", "_")
		.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		.replaceAll("[^A-Za-z0-9\\. _-]", "_");
    }

    /**
     * Formatta la data di creazione dell'elenco in una stringa nel formato "yyyyMMdd"
     *
     * @param dtCreazioneElenco la data di creazione dell'elenco
     *
     * @return la data formattata come stringa
     */
    public static String formatDateAsStr(Date dtToConvert) {
	// Convert java.util.Date to java.time.LocalDate
	LocalDate localDate = dtToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	// Format the date using DateTimeFormatter
	return localDate.format(DATE_FORMATTER);
    }

}
