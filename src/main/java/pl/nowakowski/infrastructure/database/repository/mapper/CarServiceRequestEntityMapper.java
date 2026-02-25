package pl.nowakowski.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.CarToService;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.domain.Part;
import pl.nowakowski.domain.Service;
import pl.nowakowski.domain.ServiceMechanic;
import pl.nowakowski.domain.ServicePart;
import pl.nowakowski.infrastructure.database.entity.CarServiceRequestEntity;
import pl.nowakowski.infrastructure.database.entity.ServiceMechanicEntity;
import pl.nowakowski.infrastructure.database.entity.ServicePartEntity;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarServiceRequestEntityMapper {

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "serviceMechanics", ignore = true)
    @Mapping(target = "serviceParts", ignore = true)
    CarServiceRequest mapFromEntity(CarServiceRequestEntity entity);

    default CarServiceRequest mapFromEntityWithCar(CarServiceRequestEntity entity) {
        return mapFromEntity(entity)
            .withCar(CarToService.builder()
                .vin(entity.getCar().getVin())
                .build());
    }
    
    /**
     * Maps entity with all details including service mechanics and parts.
     * This is needed for the mechanic work page to show completed work.
     * Groups by entity ID and sums quantities to handle duplicates from EntityGraph cartesian product.
     */
    default CarServiceRequest mapFromEntityWithDetails(CarServiceRequestEntity entity) {
        // Map service mechanics - group by ID and sum quantities
        Set<ServiceMechanic> serviceMechanics = entity.getServiceMechanics().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                ServiceMechanicEntity::getServiceMechanicId,
                Collectors.summingInt(sm -> sm.getQuantity() != null && sm.getQuantity() > 0 ? sm.getQuantity() : 1)
            ))
            .entrySet().stream()
            .map(entry -> {
                Integer serviceMechanicId = entry.getKey();
                Integer totalQuantity = entry.getValue();
                // Find the entity to get other fields
                ServiceMechanicEntity sm = entity.getServiceMechanics().stream()
                    .filter(e -> e.getServiceMechanicId().equals(serviceMechanicId))
                    .findFirst()
                    .orElseThrow();
                return ServiceMechanic.builder()
                    .serviceMechanicId(serviceMechanicId)
                    .hours(sm.getHours())
                    .comment(sm.getComment())
                    .quantity(totalQuantity)
                    .mechanic(sm.getMechanic() != null ? Mechanic.builder()
                        .name(sm.getMechanic().getName())
                        .surname(sm.getMechanic().getSurname())
                        .pesel(sm.getMechanic().getPesel())
                        .build() : null)
                    .service(sm.getService() != null ? Service.builder()
                        .serviceCode(sm.getService().getServiceCode())
                        .description(sm.getService().getDescription())
                        .price(sm.getService().getPrice())
                        .build() : null)
                    .build();
            })
            .collect(Collectors.toSet());
        
        // Map service parts - group by ID and sum quantities
        Set<ServicePart> serviceParts = entity.getServiceParts().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                ServicePartEntity::getServicePartId,
                Collectors.summingInt(sp -> sp.getQuantity() != null && sp.getQuantity() > 0 ? sp.getQuantity() : 1)
            ))
            .entrySet().stream()
            .map(entry -> {
                Integer servicePartId = entry.getKey();
                Integer totalQuantity = entry.getValue();
                // Find the entity to get other fields
                ServicePartEntity sp = entity.getServiceParts().stream()
                    .filter(e -> e.getServicePartId().equals(servicePartId))
                    .findFirst()
                    .orElseThrow();
                return ServicePart.builder()
                    .servicePartId(servicePartId)
                    .quantity(totalQuantity)
                    .part(sp.getPart() != null ? Part.builder()
                        .serialNumber(sp.getPart().getSerialNumber())
                        .description(sp.getPart().getDescription())
                        .price(sp.getPart().getPrice())
                        .build() : null)
                    .build();
            })
            .collect(Collectors.toSet());
        
        return CarServiceRequest.builder()
            .carServiceRequestId(entity.getCarServiceRequestId())
            .carServiceRequestNumber(entity.getCarServiceRequestNumber())
            .receivedDateTime(entity.getReceivedDateTime())
            .completedDateTime(entity.getCompletedDateTime())
            .customerComment(entity.getCustomerComment())
            .car(CarToService.builder()
                .vin(entity.getCar() != null ? entity.getCar().getVin() : null)
                .brand(entity.getCar() != null ? entity.getCar().getBrand() : null)
                .model(entity.getCar() != null ? entity.getCar().getModel() : null)
                .year(entity.getCar() != null ? entity.getCar().getYear() : null)
                .build())
            .serviceMechanics(serviceMechanics)
            .serviceParts(serviceParts)
            .build();
    }

    @Mapping(target = "customer.address", ignore = true)
    @Mapping(target = "customer.carServiceRequests", ignore = true)
    CarServiceRequestEntity mapToEntity(CarServiceRequest request);
}
