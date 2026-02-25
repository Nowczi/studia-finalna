package pl.nowakowski.business.dao;

import pl.nowakowski.domain.CarHistory;
import pl.nowakowski.domain.CarToService;

import java.util.List;
import java.util.Optional;

public interface CarToServiceDAO {

    Optional<CarToService> findCarToServiceByVin(String vin);

    CarToService saveCarToService(CarToService car);

    CarHistory findCarHistoryByVin(String vin);

    List<CarToService> findAll();
}
