package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import pl.nowakowski.api.dto.PartDTO;
import pl.nowakowski.domain.Part;

@Mapper(componentModel = "spring")
public interface PartMapper {

    PartDTO map(Part part);
}
