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

import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.converter.YesNoBooleanConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "REQUESTS")
public class Requests implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idRequest;
    private String uuid;
    private LocalDateTime dtInsert;
    private LocalDateTime dtStart;
    private LocalDateTime dtFinish;
    private LocalDateTime dtLastUpdate;
    private RequestCnts.State state;
    private Filters filter;
    private Long nrObjectFounded = Long.valueOf(0); // default
    private Long nrObjectMigrated = Long.valueOf(0); // default
    private String errorDetail;
    private Boolean deleteSourceObj = Boolean.FALSE; // default
    private RequestCnts.Type migrationType; // SIP, COMP
    private String s3Tenant;
    private String s3BanckedName;
    private String hostname;

    public Requests() {
	// constructor
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REQUEST")
    public Long getIdRequest() {
	return idRequest;
    }

    public void setIdRequest(Long idRequest) {
	this.idRequest = idRequest;
    }

    @Column(name = "UUID", nullable = false)
    public String getUuid() {
	return uuid;
    }

    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    @Column(name = "DT_INSERT", nullable = false)
    public LocalDateTime getDtInsert() {
	return dtInsert;
    }

    public void setDtInsert(LocalDateTime dtInsert) {
	this.dtInsert = dtInsert;
    }

    @Column(name = "DT_START", nullable = true)
    public LocalDateTime getDtStart() {
	return dtStart;
    }

    public void setDtStart(LocalDateTime dtStart) {
	this.dtStart = dtStart;
    }

    @Column(name = "DT_FINISH", nullable = true)
    public LocalDateTime getDtFinish() {
	return dtFinish;
    }

    public void setDtFinish(LocalDateTime dtFinish) {
	this.dtFinish = dtFinish;
    }

    @Column(name = "DT_LAST_UPDATE", nullable = false)
    public LocalDateTime getDtLastUpdate() {
	return dtLastUpdate;
    }

    public void setDtLastUpdate(LocalDateTime dtLastUpdate) {
	this.dtLastUpdate = dtLastUpdate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE", nullable = false)
    public RequestCnts.State getState() {
	return state;
    }

    public void setState(RequestCnts.State state) {
	this.state = state;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    public Filters getFilter() {
	return filter;
    }

    public void setFilter(Filters filter) {
	this.filter = filter;
    }

    @Column(name = "NR_OBJECT_FOUNDED", nullable = true)
    public Long getNrObjectFounded() {
	return nrObjectFounded;
    }

    public void setNrObjectFounded(Long nrObjectFounded) {
	this.nrObjectFounded = nrObjectFounded;
    }

    @Column(name = "NR_OBJECT_MIGRATED", nullable = true)
    public Long getNrObjectMigrated() {
	return nrObjectMigrated;
    }

    public void setNrObjectMigrated(Long nrObjectMigrated) {
	this.nrObjectMigrated = nrObjectMigrated;
    }

    @Lob
    @Column(name = "ERROR_DETAIL", nullable = true)
    public String getErrorDetail() {
	return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
	this.errorDetail = errorDetail;
    }

    @Convert(converter = YesNoBooleanConverter.class)
    @Column(name = "DELETE_SOURCE_OBJ", nullable = false)
    public Boolean getDeleteSourceObj() {
	return deleteSourceObj;
    }

    public void setDeleteSourceObj(Boolean deleteSourceObj) {
	this.deleteSourceObj = deleteSourceObj;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MIGRATION_TYPE", nullable = false)
    public RequestCnts.Type getMigrationType() {
	return migrationType;
    }

    public void setMigrationType(RequestCnts.Type migrationType) {
	this.migrationType = migrationType;
    }

    @Column(name = "S3_TENANT", nullable = true)
    public String getS3Tenant() {
	return s3Tenant;
    }

    public void setS3Tenant(String s3Tenant) {
	this.s3Tenant = s3Tenant;
    }

    @Column(name = "S3_BACKEND_NAME", nullable = true)
    public String getS3BanckedName() {
	return s3BanckedName;
    }

    public void setS3BanckedName(String s3BanckedName) {
	this.s3BanckedName = s3BanckedName;
    }

    @Column(name = "HOSTNAME", nullable = true)
    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

}
