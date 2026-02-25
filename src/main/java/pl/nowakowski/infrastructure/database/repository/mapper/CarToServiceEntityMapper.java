package pl.nowakowski.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.nowakowski.domain.CarHistory;
import pl.nowakowski.domain.CarToService;
import pl.nowakowski.domain.Part;
import pl.nowakowski.infrastructure.database.entity.CarToServiceEntity;
import pl.nowakowski.infrastructure.database.entity.ServiceMechanicEntity;
import pl.nowakowski.infrastructure.database.entity.ServicePartEntity;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarToServiceEntityMapper {

    @Mapping(target = "carServiceRequests", ignore = true)
    CarToService mapFromEntity(CarToServiceEntity entity);

    default CarHistory mapFromEntity(String vin, CarToServiceEntity entity) {
        return CarHistory.builder()
            .carVin(vin)
            .carServiceRequests(entity.getCarServiceRequests().stream()
                .map(request -> CarHistory.CarServiceRequest.builder()
                    .carServiceRequestNumber(request.getCarServiceRequestNumber())
                    .receivedDateTime(request.getReceivedDateTime())
                    .completedDateTime(request.getCompletedDateTime())
                    .customerComment(request.getCustomerComment())
                    // Group ServiceMechanicEntity by ID and sum quantities to handle duplicates from EntityGraph
                    // Then expand based on total quantity
                    .serviceWorks(request.getServiceMechanics().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                            ServiceMechanicEntity::getServiceMechanicId,
                            Collectors.summingInt(sm -> sm.getQuantity() != null && sm.getQuantity() > 0 ? sm.getQuantity() : 1)
                        ))
                        .entrySet().stream()
                        .flatMap(entry -> {
                            Integer serviceMechanicId = entry.getKey();
                            Integer totalQuantity = entry.getValue();
                            // Find the entity to get other fields
                            ServiceMechanicEntity sm = request.getServiceMechanics().stream()
                                .filter(e -> e.getServiceMechanicId().equals(serviceMechanicId))
                                .findFirst()
                                .orElseThrow();
                            return IntStream.range(0, totalQuantity)
                                .mapToObj(i -> CarHistory.ServiceWork.builder()
                                    .serviceCode(sm.getService().getServiceCode())
                                    .description(sm.getService().getDescription())
                                    .price(sm.getService().getPrice())
                                    .mechanicName(sm.getMechanic() != null ? sm.getMechanic().getName() : "Unknown")
                                    .mechanicSurname(sm.getMechanic() != null ? sm.getMechanic().getSurname() : "")
                                    .hours(sm.getHours())
                                    .mechanicComment(sm.getComment())
                                    .build());
                        })
                        .collect(Collectors.toList()))
                    // Group ServicePartEntity by ID and sum quantities to handle duplicates from EntityGraph
                    // Then expand based on total quantity
                    .parts(request.getServiceParts().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                            ServicePartEntity::getServicePartId,
                            Collectors.summingInt(sp -> sp.getQuantity() != null && sp.getQuantity() > 0 ? sp.getQuantity() : 1)
                        ))
                        .entrySet().stream()
                        .flatMap(entry -> {
                            Integer servicePartId = entry.getKey();
                            Integer totalQuantity = entry.getValue();
                            // Find the entity to get other fields
                            ServicePartEntity sp = request.getServiceParts().stream()
                                .filter(e -> e.getServicePartId().equals(servicePartId))
                                .findFirst()
                                .orElseThrow();
                            return IntStream.range(0, totalQuantity)
                                .mapToObj(i -> Part.builder()
                                    .serialNumber(sp.getPart().getSerialNumber())
                                    .description(sp.getPart().getDescription())
                                    .price(sp.getPart().getPrice())
                                    .build());
                        })
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    CarToServiceEntity mapToEntity(CarToService car);
}
