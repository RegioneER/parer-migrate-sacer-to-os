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

package it.eng.parer.migrate.sacer.os.jpa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "OBJECT_STORAGE")
public class ObjectStorage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idObjectStorage;
    private Requests request;
    private Long pkObject;
    private ObjectStorageCnts.State state;
    private ObjectStorageCnts.ObjectType type;
    private String s3Bucket;
    private String s3Key;
    private String s3Checksum;
    private String objBase64;
    private ObjectStorageCnts.IntegrityType integrityType;
    private String errorDetail;
    private LocalDateTime dtInsert;

    public ObjectStorage() {
	// constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OBJECT_STORAGE")
    public Long getIdObjectStorage() {
	return idObjectStorage;
    }

    public void setIdObjectStorage(Long idObjectStorage) {
	this.idObjectStorage = idObjectStorage;
    }

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_REQUEST", nullable = false)
    public Requests getRequest() {
	return this.request;
    }

    public void setRequest(Requests request) {
	this.request = request;
    }

    @Column(name = "PK_OBJECT", nullable = false)
    public Long getPkObject() {
	return pkObject;
    }

    public void setPkObject(Long idObject) {
	this.pkObject = idObject;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MIGRATE_STATE", nullable = false)
    public ObjectStorageCnts.State getState() {
	return state;
    }

    public void setState(ObjectStorageCnts.State state) {
	this.state = state;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "OBJECT_TYPE", nullable = false)
    public ObjectStorageCnts.ObjectType getType() {
	return type;
    }

    public void setType(ObjectStorageCnts.ObjectType type) {
	this.type = type;
    }

    @Column(name = "S3_BUCKET", nullable = true)
    public String getS3Bucket() {
	return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
	this.s3Bucket = s3Bucket;
    }

    @Column(name = "S3_KEY", nullable = true)
    public String getS3Key() {
	return s3Key;
    }

    public void setS3Key(String s3Key) {
	this.s3Key = s3Key;
    }

    @Lob
    @Column(name = "ERROR_DETAIL", nullable = true)
    public String getErrorDetail() {
	return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
	this.errorDetail = errorDetail;
    }

    @Column(name = "OBJ_BASE64", nullable = true)
    public String getObjBase64() {
	return objBase64;
    }

    public void setObjBase64(String objBase64) {
	this.objBase64 = objBase64;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "INTEGRITY_TYPE", nullable = true)
    public ObjectStorageCnts.IntegrityType getIntegrityType() {
	return integrityType;
    }

    public void setIntegrityType(ObjectStorageCnts.IntegrityType integrityType) {
	this.integrityType = integrityType;
    }

    @Column(name = "S3_CHECKSUM", nullable = true)
    public String getS3Checksum() {
	return s3Checksum;
    }

    public void setS3Checksum(String s3Checksum) {
	this.s3Checksum = s3Checksum;
    }

    @Column(name = "DT_INSERT", nullable = false)
    public LocalDateTime getDtInsert() {
	return dtInsert;
    }

    public void setDtInsert(LocalDateTime dtInsert) {
	this.dtInsert = dtInsert;
    }

}
