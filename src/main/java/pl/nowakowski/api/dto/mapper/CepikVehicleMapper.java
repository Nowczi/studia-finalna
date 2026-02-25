package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import pl.nowakowski.api.dto.CepikVehicleDTO;
import pl.nowakowski.domain.CepikVehicle;

@Mapper(componentModel = "spring")
public interface CepikVehicleMapper {


    CepikVehicleDTO map(CepikVehicle cepikVehicle);
}
