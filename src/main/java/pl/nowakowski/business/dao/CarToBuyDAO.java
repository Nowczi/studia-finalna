package pl.nowakowski.business.dao;

import pl.nowakowski.domain.CarToBuy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CarToBuyDAO {

    Optional<CarToBuy> findCarToBuyByVin(String vin);

    List<CarToBuy> findAvailable();
    
    CarToBuy saveCarToBuy(CarToBuy carToBuy);
    
    void deleteCarToBuy(String vin);

    List<CarToBuy> searchAvailableCars(String brand, String model, Integer yearFrom, Integer yearTo, 
                                        String color, BigDecimal priceFrom, BigDecimal priceTo);
}
