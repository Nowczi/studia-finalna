package pl.nowakowski.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import pl.nowakowski.domain.ServiceMechanic;
import pl.nowakowski.infrastructure.database.entity.ServiceMechanicEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMechanicEntityMapper {

    ServiceMechanicEntity mapToEntity(ServiceMechanic serviceMechanic);
}
