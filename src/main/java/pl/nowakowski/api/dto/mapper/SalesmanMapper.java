package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.nowakowski.api.dto.SalesmanDTO;
import pl.nowakowski.domain.Salesman;

@Mapper(componentModel = "spring")
public interface SalesmanMapper {

    @Mapping(source = "userName", target = "userName")
    SalesmanDTO map(final Salesman salesman);
}
