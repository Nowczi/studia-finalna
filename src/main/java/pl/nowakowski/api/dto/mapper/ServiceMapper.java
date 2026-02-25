package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import pl.nowakowski.api.dto.ServiceDTO;
import pl.nowakowski.domain.Service;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceDTO map(Service service);
}
