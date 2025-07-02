package it.eng.parer.migrate.sacer.os.base.impl;

import static it.eng.parer.migrate.sacer.os.base.utils.MigrateUtils.getHostname;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.migrate.sacer.os.base.IMigrateOsDao;
import it.eng.parer.migrate.sacer.os.base.IMigrateOsService;
import it.eng.parer.migrate.sacer.os.base.dto.FilterDto;
import it.eng.parer.migrate.sacer.os.base.dto.RequestDto;
import it.eng.parer.migrate.sacer.os.base.model.MigrateRequest;
import it.eng.parer.migrate.sacer.os.exceptions.AppBadRequestException;
import it.eng.parer.migrate.sacer.os.exceptions.AppGenericRuntimeException;
import it.eng.parer.migrate.sacer.os.exceptions.ErrorCategory;
import it.eng.parer.migrate.sacer.os.jpa.constraint.ObjectStorageCnts;
import it.eng.parer.migrate.sacer.os.jpa.constraint.RequestCnts;
import it.eng.parer.migrate.sacer.os.jpa.entity.ObjectStorage;
import it.eng.parer.migrate.sacer.os.jpa.entity.Requests;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByCol;
import it.eng.parer.migrate.sacer.os.runner.util.EndPointCostants.OsRequestOrderByTo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class MigrateOsService implements IMigrateOsService {

    private static final Logger log = LoggerFactory.getLogger(MigrateOsService.class);

    @ConfigProperty(name = "s3.backend.name")
    String nmBackend;

    @ConfigProperty(name = "s3.tenant.name")
    String tenant;

    @Inject
    IMigrateOsDao osBaseDao;

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppGenericRuntimeException.class })
    public void updateOsRequest(Long idRequest, RequestCnts.State state,
	    Optional<LocalDateTime> dtStart, Optional<LocalDateTime> dtLastUpdate,
	    Optional<LocalDateTime> dtFinish, Optional<Long> nrFounded, Optional<Long> nrDone,
	    Optional<String> errorDetail, Optional<String> hostname) {
	// call dao
	osBaseDao.updateRequest(idRequest, state, dtStart, dtLastUpdate, dtFinish, nrFounded,
		nrDone, errorDetail, hostname);
    }

    @Transactional(value = TxType.REQUIRED, rollbackOn = AppGenericRuntimeException.class)
    public boolean testJobskipExecution(RequestCnts.Type type) {
	//
	List<Requests> result = osBaseDao.findRequestsByStateTypeDtStartFinish(
		Optional.of(RequestCnts.State.REGISTERED), Optional.empty(), Optional.empty(),
		Optional.of(type), OsRequestOrderByCol.DT_INSERT.toString(),
		OsRequestOrderByTo.ASC.toString(), 1);
	return result.isEmpty();
    }

    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public Requests findAndLockOsRequestBeforeStart(RequestCnts.Type type) {
	// call dao select with lock on write (avoid concurrency)
	osBaseDao.lockRequests();
	//
	List<Requests> result = osBaseDao.findRequestsByStateTypeDtStartFinish(
		Optional.of(RequestCnts.State.REGISTERED), Optional.empty(), Optional.empty(),
		Optional.of(type), OsRequestOrderByCol.DT_INSERT.toString(),
		OsRequestOrderByTo.ASC.toString(), 1);
	if (result.isEmpty()) {
	    throw AppGenericRuntimeException.builder().category(ErrorCategory.NOT_FOUND_ENTITY)
		    .message("Non risultano richieste in stato {0}, non eseguo job",
			    RequestCnts.State.REGISTERED)
		    .build();
	} else {
	    //
	    Requests request = result.get(0);
	    log.atInfo().log("Presente richiesta UUID {} con stato {}, eseguo job",
		    request.getUuid(), RequestCnts.State.REGISTERED);
	    // update state to WAITING
	    osBaseDao.updateRequest(request.getIdRequest(), RequestCnts.State.WAITING,
		    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		    Optional.empty(), Optional.empty(), getHostname());

	    return request;
	}
    }

    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class })
    public List<RequestDto> findOsRequests(String state, String type, LocalDate dtstart,
	    LocalDate dtfinish, String orderbycol, String orderbyto, Integer maxresult) {
	List<RequestDto> dtos = new ArrayList<>();
	List<Requests> requests = osBaseDao.findRequestsByStateTypeDtStartFinish(
		StringUtils.isNotBlank(state) ? Optional.of(RequestCnts.State.valueOf(state))
			: Optional.empty(),
		Optional.ofNullable(dtstart), Optional.ofNullable(dtfinish),
		StringUtils.isNotBlank(type) ? Optional.of(RequestCnts.Type.valueOf(type))
			: Optional.empty(),
		Optional.ofNullable(orderbycol).orElse(OsRequestOrderByCol.DT_INSERT.toString()),
		Optional.ofNullable(orderbyto).orElse(OsRequestOrderByTo.ASC.toString()),
		Optional.ofNullable(maxresult).orElse(100));
	// create dto list
	requests.forEach(r -> dtos.add(new RequestDto(r)));
	return dtos;
    }

    @Transactional(value = TxType.REQUIRED, rollbackOn = {
	    AppGenericRuntimeException.class, AppBadRequestException.class })
    public List<RequestDto> registerOsRequestByType(List<MigrateRequest> osRequests,
	    RequestCnts.Type type) {
	// verify duplicate inside same request
	if (osRequests.stream()
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet().stream().anyMatch(e -> e.getValue() > 1)) {
	    throw AppBadRequestException.builder()
		    .message("Richiesta non valida, presenti uno o più filtri con stesso valore")
		    .build();
	}
	// result
	List<RequestDto> requestDtos = new ArrayList<>();
	// each one
	osRequests.forEach(osRequest -> {
	    FilterDto filterDto = new FilterDto(osRequest);
	    List<String> uuids = osBaseDao.checkRequestsByStateAndFilter(filterDto, type);
	    // register on table
	    if (uuids.isEmpty()) {
		Requests request = osBaseDao.createRequest(osRequest, type, tenant, nmBackend);
		// filter
		requestDtos.add(new RequestDto(request));
	    } else {
		// not registered !
		requestDtos.add(new RequestDto(type, osRequest.deletesrc, tenant, nmBackend,
			filterDto,
			MessageFormat.format(
				"Richiesta non valida, il filtro richiesto risulta in lavorazione (verificare stato richiesta/e con uuid = [{0}])",
				String.join(", ", uuids))));
	    }
	});

	return requestDtos;
    }

    @Override
    @Transactional(value = TxType.REQUIRED)
    public RequestDto findOsRequestByUuid(final String uuid) {
	// call dao
	Requests request = osBaseDao.findRequestByUuid(uuid);
	return new RequestDto(request);
    }

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppGenericRuntimeException.class })
    public ObjectStorage createOsObjectStorageOfObject(Long idRequest, Long pkObject,
	    ObjectStorageCnts.State state, ObjectStorageCnts.ObjectType type,
	    Optional<String> bucketName, Optional<String> key, Optional<String> objBase64,
	    Optional<String> s3checksum, Optional<ObjectStorageCnts.IntegrityType> integrityType,
	    Optional<String> errorDetail) {
	// call dao
	return osBaseDao.createObjectStorage(idRequest, pkObject, state, type, bucketName, key,
		objBase64, s3checksum, integrityType, errorDetail);
    }

    @Override
    @Transactional(value = TxType.REQUIRES_NEW, rollbackOn = {
	    AppGenericRuntimeException.class })
    public RequestDto getRequestById(Long idRequest) {
	// call dao
	Requests request = osBaseDao.findRequestById(idRequest);
	return new RequestDto(request);
    }

}
