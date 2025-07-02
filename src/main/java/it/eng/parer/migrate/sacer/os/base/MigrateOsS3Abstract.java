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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.migrate.sacer.os.base;

import java.io.InputStream;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import it.eng.parer.migrate.sacer.os.base.utils.Costants.ChecksumAlghoritm;
import it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public abstract class MigrateOsS3Abstract {

    @Inject
    S3Client s3Client;

    // MD5 vs SHA-x vs CRC32C
    @ConfigProperty(name = "s3.obj.checksum.algorithm")
    ChecksumAlghoritm checksumlAlghoritm;

    @ConfigProperty(name = "quarkus.uuid")
    String instanceUUID;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    /**
     * Effettua il trasferimento su bucket dell'input stream via S3 trasmetto lo SHA256 del file
     *
     * @param is         input stream file
     * @param size       dimensione file
     * @param objbase64  sha256 calcolato (Base64)
     * @param bucketName nome bucket
     * @param key        chiave calcolata
     *
     * @return {@link IObjectStorageResource} ottenuta dalla response a fronte del trasferimento via
     *         S3
     */
    protected IObjectStorageResource s3PutObjectAsFile(InputStream is, long size, String objbase64,
	    String bucketName, String key) {
	Builder putObjectBuilder = PutObjectRequest.builder().bucket(bucketName).key(key);
	// metadata
	putObjectBuilder.metadata(MigrateUtils.defaultS3Metadata(instanceUUID));
	// data integrity
	switch (checksumlAlghoritm) {
	case SHA256:
	    putObjectBuilder.checksumSHA256(objbase64);
	    break;
	case CRC32C:
	    putObjectBuilder.checksumCRC32C(objbase64);
	    break;
	default:
	    putObjectBuilder.contentMD5(objbase64);
	    break;
	}
	//
	PutObjectRequest objectRequest = putObjectBuilder.build();
	PutObjectResponse objectRequestResponse = s3Client.putObject(objectRequest,
		RequestBody.fromInputStream(is, size));

	return fromObjRespToIOStorageResouce(objectRequestResponse, bucketName, key, objbase64);
    }

    private IObjectStorageResource fromObjRespToIOStorageResouce(PutObjectResponse response,
	    String bucketName, String key, String objbase64) {
	return new IObjectStorageResource() {
	    @Override
	    public String getS3Key() {
		return key;
	    }

	    @Override
	    public String getETag() {
		return response.eTag();
	    }

	    @Override
	    public String getObjBase64() {
		return objbase64;
	    }

	    @Override
	    public String getSHA256() {
		return response.checksumSHA256();
	    }

	    @Override
	    public String getS3Bucket() {
		return bucketName;
	    }

	    @Override
	    public String getS3Checksum() {
		return checksumlAlghoritm.name();
	    }

	    @Override
	    public String getTenant() {
		return tenant;
	    }
	};
    }

    protected ChecksumAlghoritm getIntegrityType() {
	return checksumlAlghoritm;
    }

    protected String getInstanceUUID() {
	return instanceUUID;
    }

    protected String getTenant() {
	return tenant;
    }

}
