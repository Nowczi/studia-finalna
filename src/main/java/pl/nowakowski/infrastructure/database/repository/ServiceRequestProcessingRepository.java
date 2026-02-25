package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.ServiceRequestProcessingDAO;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.ServiceMechanic;
import pl.nowakowski.domain.ServicePart;
import pl.nowakowski.infrastructure.database.entity.CarServiceRequestEntity;
import pl.nowakowski.infrastructure.database.entity.PartEntity;
import pl.nowakowski.infrastructure.database.entity.ServiceMechanicEntity;
import pl.nowakowski.infrastructure.database.entity.ServicePartEntity;
import pl.nowakowski.infrastructure.database.repository.jpa.CarServiceRequestJpaRepository;
import pl.nowakowski.infrastructure.database.repository.jpa.PartJpaRepository;
import pl.nowakowski.infrastructure.database.repository.jpa.ServiceMechanicJpaRepository;
import pl.nowakowski.infrastructure.database.repository.jpa.ServicePartJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.ServiceMechanicEntityMapper;
import pl.nowakowski.infrastructure.database.repository.mapper.ServicePartEntityMapper;

import java.util.Objects;


@Repository
@AllArgsConstructor
public class ServiceRequestProcessingRepository implements ServiceRequestProcessingDAO {

    private final ServiceMechanicJpaRepository serviceMechanicJpaRepository;
    private final CarServiceRequestJpaRepository carServiceRequestJpaRepository;
    private final PartJpaRepository partJpaRepository;
    private final ServicePartJpaRepository servicePartJpaRepository;
    private final ServiceMechanicEntityMapper serviceMechanicEntityMapper;
    private final ServicePartEntityMapper servicePartEntityMapper;

    @Override
    @Transactional
    public void process(
        CarServiceRequest serviceRequest,
        ServiceMechanic serviceMechanic
    ) {
        ServiceMechanicEntity serviceMechanicEntity = serviceMechanicEntityMapper.mapToEntity(serviceMechanic);
        serviceMechanicJpaRepository.saveAndFlush(serviceMechanicEntity);
        if (Objects.nonNull(serviceRequest.getCompletedDateTime())) {
            CarServiceRequestEntity carServiceRequestEntity = carServiceRequestJpaRepository
                .findById(serviceRequest.getCarServiceRequestId())
                .orElseThrow();
            carServiceRequestEntity.setCompletedDateTime(serviceRequest.getCompletedDateTime());
            carServiceRequestJpaRepository.saveAndFlush(carServiceRequestEntity);
        }
    }

    @Override
    @Transactional
    public void process(
        CarServiceRequest serviceRequest,
        ServiceMechanic serviceMechanic,
        ServicePart servicePart
    ) {
        PartEntity partEntity = partJpaRepository.findById(servicePart.getPart().getPartId()).orElseThrow();
        ServicePartEntity servicePartEntity = servicePartEntityMapper.mapToEntity(servicePart);
        servicePartEntity.setPart(partEntity);
        servicePartJpaRepository.saveAndFlush(servicePartEntity);
        process(serviceRequest, serviceMechanic);
    }

    @Override
    @Transactional
    public void markServiceRequestAsCompleted(CarServiceRequest serviceRequest) {
        // Only update the service request's completedDateTime
        // Do NOT create any service_mechanic or service_part entries
        if (Objects.nonNull(serviceRequest.getCompletedDateTime())) {
            CarServiceRequestEntity carServiceRequestEntity = carServiceRequestJpaRepository
                .findById(serviceRequest.getCarServiceRequestId())
                .orElseThrow();
            carServiceRequestEntity.setCompletedDateTime(serviceRequest.getCompletedDateTime());
            carServiceRequestJpaRepository.saveAndFlush(carServiceRequestEntity);
        }
    }
}
