package it.eng.parer.migrate.sacer.os.base.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import it.eng.parer.migrate.sacer.os.base.IMigrateOsDao;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppEntityNotFoundException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts.State;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts.Type;
import it.eng.parer.migrate.sacer.os.jpa.entity.Filters;
import it.eng.parer.migrate.sacer.os.jpa.entity.ObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;

@ApplicationScoped
public class MigrateOsDao implements IMigrateOsDao {

    @Inject
    protected EntityManager entityManager;

    protected static final String QUERY_AND = " and ";

    @Override
    public void lockRequests() {
	try {
	    entityManager.createQuery("from Requests").setLockMode(LockModeType.PESSIMISTIC_WRITE)
		    .setHint("jakarta.persistence.query.timeout", 600000).getResultList(); // 10
											   // minutes
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore estrazione richiesta in stato {0}",
			    RequestCnts.State.REGISTERED)
		    .build();
	}
    }

    @Override
    public void updateRequest(Long idRequest, State state, Optional<LocalDateTime> dtStart,
	    Optional<LocalDateTime> dtLastUpdate, Optional<LocalDateTime> dtFinish,
	    Optional<Long> nrFounded, Optional<Long> nrDone, Optional<String> errorDetail,
	    Optional<String> hostname) {
	try {
	    Requests request = entityManager.find(Requests.class, idRequest);
	    // non null
	    request.setState(state);
	    // nullable
	    if (dtStart.isPresent()) {
		request.setDtStart(dtStart.get());
	    }
	    // nullable
	    if (dtLastUpdate.isPresent()) {
		request.setDtLastUpdate(dtLastUpdate.get());
	    }
	    // nullable
	    if (dtFinish.isPresent()) {
		request.setDtFinish(dtFinish.get());
	    }
	    // nullable
	    if (nrFounded.isPresent()) {
		request.setNrObjectFounded(nrFounded.get());
	    }
	    // nullable
	    if (nrDone.isPresent()) {
		request.setNrObjectMigrated(nrDone.get());
	    }
	    //
	    if (errorDetail.isPresent()) {
		request.setErrorDetail(errorDetail.get());
	    }
	    // nullable
	    if (hostname.isPresent()) {
		request.setHostname(hostname.get());
	    }

	    entityManager.persist(request);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore in fase di aggiornamento richiesta con id {0,number,#}",
			    idRequest)
		    .build();
	}
    }

    @Override
    public List<Requests> findRequestsByStateTypeDtStartFinish(Optional<RequestCnts.State> state,
	    Optional<LocalDate> dtstart, Optional<LocalDate> dtfinish,
	    Optional<RequestCnts.Type> type, String orderByCol, String orderByTo, int maxresult) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("from Requests ");

	    if (state.isPresent() || dtstart.isPresent() || dtfinish.isPresent()
		    || type.isPresent()) {
		queryStr.append("where ");
	    }

	    // dinamyc params
	    if (state.isPresent()) {
		queryStr.append("state = :state").append(QUERY_AND);
	    }
	    if (dtstart.isPresent()) {
		queryStr.append("to_char(dtStart, 'YYYY-MM-DD') >= to_char(:dtStart, 'YYYY-MM-DD')")
			.append(QUERY_AND);
	    }
	    if (dtfinish.isPresent()) {
		queryStr.append(
			"to_char(dtFinish, 'YYYY-MM-DD') <= to_char(:dtFinish, 'YYYY-MM-DD')")
			.append(QUERY_AND);
	    }
	    if (type.isPresent()) {
		queryStr.append("migrationType = :migrationType");
	    }
	    // clean
	    String queryClean = StringUtils.removeEnd(queryStr.toString(), QUERY_AND);
	    // order by to and versus
	    queryClean = queryClean.concat(" order by ")
		    .concat(CaseUtils.toCamelCase(orderByCol, false, '_'))
		    .concat(" " + orderByTo.toLowerCase() + " ");

	    //
	    TypedQuery<Requests> query = entityManager.createQuery(queryClean, Requests.class);
	    query.setMaxResults(maxresult);

	    if (state.isPresent()) {
		query.setParameter("state", state.get());
	    }
	    if (dtstart.isPresent()) {
		query.setParameter("dtStart", dtstart.get().atStartOfDay());
	    }
	    if (dtfinish.isPresent()) {
		query.setParameter("dtFinish", dtfinish.get().atStartOfDay());
	    }
	    if (type.isPresent()) {
		query.setParameter("migrationType", type.get());
	    }

	    return query.getResultList();

	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca delle richieste").build();
	}
    }

    @Override
    public Requests findRequestByUuid(final String uuid) {
	try {
	    Query query = entityManager.createQuery("from Requests where uuid = :uuid");
	    query.setParameter("uuid", uuid);
	    return (Requests) query.getSingleResult();
	} catch (NoResultException e) {
	    throw AppEntityNotFoundException.builder().uuidMigrateReq(uuid).cause(e)
		    .message("Richiesta con uuid {0} non trovata", uuid).build();
	} catch (NonUniqueResultException e) {
	    throw AppGenericRuntimeException.builder().uuidMigrateReq(uuid)
		    .category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore interno nella fase ricerca della richiesta di migrazione con uuid {0}",
			    uuid)
		    .build();
	}
    }

    @Override
    public Requests findRequestById(final Long idRequest) {
	try {
	    return entityManager.find(Requests.class, idRequest);
	} catch (IllegalArgumentException e) {
	    throw AppGenericRuntimeException.builder().cause(e)
		    .message("Richiesta con id {0,number,#} non trovata", idRequest).build();
	}
    }

    @Override
    public Requests createRequest(@Valid MigrateRequest osRequest, Type type, String s3Tenant,
	    String s3BackendName) {
	Requests request;
	try {
	    request = new Requests();
	    // set uuid
	    request.setUuid(UUID.randomUUID().toString());
	    // set state
	    request.setState(RequestCnts.State.REGISTERED);
	    // set dt insert
	    request.setDtInsert(
		    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
	    // set last update
	    request.setDtLastUpdate(
		    LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
	    // clean xml ?
	    request.setDeleteSourceObj(osRequest.deletesrc);
	    //
	    request.setMigrationType(type);
	    //
	    request.setS3Tenant(s3Tenant);
	    request.setS3BanckedName(s3BackendName);

	    // create filter
	    Filters filter = new Filters();
	    filter.setRowlimit(osRequest.rowlimit);
	    // nullable
	    filter.setIdUnitadoc(osRequest.idunitadoc);
	    // nullable
	    filter.setIdDoc(osRequest.iddoc);
	    // nullable
	    filter.setIdCompDoc(osRequest.idcomp);
	    // nullable
	    filter.setIdStrut(osRequest.idstrut);
	    // nullable
	    filter.setIdVerIndiceAip(osRequest.idverindiceaip);
	    // nullable
	    filter.setDtApertura(osRequest.dtapertura);
	    // nullable
	    filter.setDtAperturaYY(osRequest.dtaperturayy);
	    // nullable
	    filter.setIdSessioneVers(osRequest.idsessionvers);
	    // nullable
	    filter.setIdVerSerie(osRequest.idverserie);
	    // nullable
	    filter.setIdElencoVers(osRequest.idelencovers);

	    filter.setRequest(request);

	    entityManager.persist(request);

	    // set filter on request
	    request.setFilter(filter);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore in fase di registrazione richiesta").build();
	}

	return request;
    }

    @Override
    public List<String> checkRequestsByStateAndFilter(FilterDto filterDto, Type type) {
	StringBuilder queryStr = new StringBuilder();
	try {
	    queryStr.append("select req.uuid ");

	    queryStr.append("from Requests req join req.filter filter ");
	    queryStr.append("where ");
	    queryStr.append("req.state not in (:state)").append(QUERY_AND); // fixed
	    queryStr.append("req.migrationType = :migrationType").append(QUERY_AND); // fixed

	    // dinamyc params
	    if (!Objects.isNull(filterDto.getIdUnitadoc())) {
		queryStr.append("filter.idUnitadoc = :idUnitadoc").append(QUERY_AND);
	    }
	    if (!Objects.isNull(filterDto.getIdDoc())) {
		queryStr.append("filter.idDoc = :idDoc").append(QUERY_AND);
	    }
	    if (!Objects.isNull(filterDto.getIdSessioneVers())) {
		queryStr.append("filter.idSessioneVers = :idSessioneVers").append(QUERY_AND);
	    }
	    if (!Objects.isNull(filterDto.getIdCompDoc())) {
		queryStr.append("filter.idCompDoc = :idCompDoc").append(QUERY_AND);
	    }
	    if (!Objects.isNull(filterDto.getIdStrut())) {
		queryStr.append("filter.idStrut = :idStrut");
	    }
	    if (!Objects.isNull(filterDto.getIdVerIndiceAip())) {
		queryStr.append("filter.idVerIndiceAip = :idVerIndiceAip").append(QUERY_AND);
	    }
	    if (!Objects.isNull(filterDto.getIdVerSerie())) {
		queryStr.append("filter.idVerSerie = :idVerSerie").append(QUERY_AND);
	    }

	    // clean
	    String queryClean = StringUtils.removeEnd(queryStr.toString(), QUERY_AND);

	    //
	    TypedQuery<String> query = entityManager.createQuery(queryClean, String.class);
	    query.setParameter("state",
		    Arrays.asList(RequestCnts.State.ERROR, RequestCnts.State.FINISHED));
	    query.setParameter("migrationType", type);

	    if (!Objects.isNull(filterDto.getIdUnitadoc())) {
		query.setParameter("idUnitadoc", filterDto.getIdUnitadoc());
	    }
	    if (!Objects.isNull(filterDto.getIdDoc())) {
		query.setParameter("idDoc", filterDto.getIdDoc());
	    }
	    if (!Objects.isNull(filterDto.getIdSessioneVers())) {
		query.setParameter("idSessioneVers", filterDto.getIdSessioneVers());
	    }
	    if (!Objects.isNull(filterDto.getIdCompDoc())) {
		query.setParameter("idCompDoc", filterDto.getIdCompDoc());
	    }
	    if (!Objects.isNull(filterDto.getIdStrut())) {
		query.setParameter("idStrut", filterDto.getIdStrut());
	    }
	    if (!Objects.isNull(filterDto.getIdVerIndiceAip())) {
		query.setParameter("idVerIndiceAip", filterDto.getIdVerIndiceAip());
	    }
	    if (!Objects.isNull(filterDto.getIdVerSerie())) {
		query.setParameter("idVerSerie", filterDto.getIdVerSerie());
	    }
	    return query.getResultList();
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().cause(e).category(ErrorCategory.HIBERNATE)
		    .message("Errore interno nella fase ricerca delle richieste con stesso filtro")
		    .build();
	}
    }

    @Override
    public ObjectStorage createObjectStorage(Long idRequest, Long pkObject,
	    ObjectStorageCnts.State state, ObjectStorageCnts.ObjectType type,
	    Optional<String> bucketName, Optional<String> key, Optional<String> objBase64,
	    Optional<String> s3checksum, Optional<ObjectStorageCnts.IntegrityType> integrityType,
	    Optional<String> errorDetail) {
	ObjectStorage objectStorage;
	try {
	    // get request
	    Requests request = entityManager.find(Requests.class, idRequest);
	    //
	    objectStorage = new ObjectStorage();
	    objectStorage.setPkObject(pkObject);
	    objectStorage.setType(type);
	    objectStorage.setState(state);
	    objectStorage.setRequest(request);
	    objectStorage.setDtInsert(LocalDateTime.now()); // sysdate

	    if (bucketName.isPresent()) {
		objectStorage.setS3Bucket(bucketName.get());
	    }
	    if (key.isPresent()) {
		objectStorage.setS3Key(key.get());
	    }
	    if (objBase64.isPresent()) {
		objectStorage.setObjBase64(objBase64.get());
	    }
	    if (s3checksum.isPresent()) {
		objectStorage.setS3Checksum(s3checksum.get());
	    }
	    if (integrityType.isPresent()) {
		objectStorage.setIntegrityType(integrityType.get());
	    }
	    if (errorDetail.isPresent()) {
		objectStorage.setErrorDetail(errorDetail.get());
	    }

	    entityManager.persist(objectStorage);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message(
			    "Errore in fase di registrazione migrazione con id richiesta {0,number,#} per oggetto con pk {1,number,#} di tipo {2}",
			    idRequest, pkObject, type)
		    .build();
	}

	return objectStorage;
    }

    @Override
    public void updateObjectStorage(Long idObjectStorage, ObjectStorageCnts.State state,
	    Optional<String> bucketName, Optional<String> key, Optional<String> objBase64,
	    Optional<String> s3checksum, Optional<ObjectStorageCnts.IntegrityType> integrityType,
	    Optional<String> errorDetail) {
	try {
	    // get request
	    ObjectStorage sipObjectStorage = entityManager.find(ObjectStorage.class,
		    idObjectStorage);
	    //
	    sipObjectStorage.setState(state);

	    if (bucketName.isPresent()) {
		sipObjectStorage.setS3Bucket(bucketName.get());
	    }
	    if (key.isPresent()) {
		sipObjectStorage.setS3Key(key.get());
	    }
	    if (objBase64.isPresent()) {
		sipObjectStorage.setObjBase64(objBase64.get());
	    }
	    if (errorDetail.isPresent()) {
		sipObjectStorage.setErrorDetail(errorDetail.get());
	    }
	    if (s3checksum.isPresent()) {
		sipObjectStorage.setS3Checksum(s3checksum.get());
	    }
	    if (integrityType.isPresent()) {
		sipObjectStorage.setIntegrityType(integrityType.get());
	    }

	    entityManager.persist(sipObjectStorage);
	} catch (Exception e) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.HIBERNATE).cause(e)
		    .message("Errore in fase di aggiornamento migrazione id {0,number,#}",
			    idObjectStorage)
		    .build();
	}
    }
}
