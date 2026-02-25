package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.nowakowski.api.dto.MechanicDTO;
import pl.nowakowski.domain.Mechanic;

@Mapper(componentModel = "spring")
public interface MechanicMapper {

    @Mapping(source = "userName", target = "userName")
    MechanicDTO map(final Mechanic mechanic);
}
