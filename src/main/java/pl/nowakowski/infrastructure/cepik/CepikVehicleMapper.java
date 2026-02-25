package pl.nowakowski.infrastructure.cepik;

import org.springframework.stereotype.Component;
import pl.nowakowski.domain.CepikVehicle;
import pl.nowakowski.infrastructure.cepik.model.VehicleDto;

import java.util.Optional;

@Component
public class CepikVehicleMapper {

    public CepikVehicle map(String id, VehicleDto attributes) {

        var builder = CepikVehicle.builder()
                .cepikId(id);

        Optional.ofNullable(attributes)
                .ifPresent(dto -> builder
                        .brand(dto.getMarka())
                        .model(dto.getModel())
                        .type(dto.getTyp())
                        .engineCapacity(dto.getPojemnoscSkokowaSilnika())
                        .weight(dto.getMasaWlasna())
                        .fuel(dto.getRodzajPaliwa())
                );
        return builder.build();
    }

}
