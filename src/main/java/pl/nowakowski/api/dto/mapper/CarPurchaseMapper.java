package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import pl.nowakowski.api.dto.CarPurchaseDTO;
import pl.nowakowski.api.dto.CarToBuyDTO;
import pl.nowakowski.domain.CarPurchaseRequest;
import pl.nowakowski.domain.CarToBuy;

@Mapper(componentModel = "spring")
public interface CarPurchaseMapper {

     CarPurchaseRequest map(final CarPurchaseDTO dto);

    CarToBuyDTO map(CarToBuy car);
}
