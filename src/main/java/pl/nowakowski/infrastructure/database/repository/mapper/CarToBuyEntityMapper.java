package pl.nowakowski.infrastructure.database.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.nowakowski.domain.CarToBuy;
import pl.nowakowski.infrastructure.database.entity.CarToBuyEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarToBuyEntityMapper {

    @Mapping(target = "invoice", ignore = true)
    CarToBuy mapFromEntity(CarToBuyEntity entity);

    @Mapping(target = "carToBuyId", ignore = true)  // ID is auto-generated
    @Mapping(target = "invoice", ignore = true)      // Invoice is set separately
    CarToBuyEntity mapToEntity(CarToBuy car);
}