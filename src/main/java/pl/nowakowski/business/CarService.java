package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.CarToBuyDAO;
import pl.nowakowski.business.dao.CarToServiceDAO;
import pl.nowakowski.domain.CarHistory;
import pl.nowakowski.domain.CarToBuy;
import pl.nowakowski.domain.CarToService;
import pl.nowakowski.domain.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CarService {

    private final CarToBuyDAO carToBuyDAO;
    private final CarToServiceDAO carToServiceDAO;

    @Transactional
    public List<CarToBuy> findAvailableCars() {
        List<CarToBuy> availableCars = carToBuyDAO.findAvailable();
        log.info("Available cars: [{}]", availableCars.size());
        return availableCars;
    }

    @Transactional
    public List<CarToBuy> searchAvailableCars(String brand, String model, Integer yearFrom, Integer yearTo,
                                               String color, BigDecimal priceFrom, BigDecimal priceTo) {
        List<CarToBuy> availableCars = carToBuyDAO.searchAvailableCars(brand, model, yearFrom, yearTo, 
                                                                        color, priceFrom, priceTo);
        log.info("Search found cars: [{}]", availableCars.size());
        return availableCars;
    }

    @Transactional
    public CarToBuy findCarToBuy(String vin) {
        Optional<CarToBuy> carToBuyByVin = carToBuyDAO.findCarToBuyByVin(vin);
        if (carToBuyByVin.isEmpty()) {
            throw new NotFoundException("Could not find car by vin: [%s]".formatted(vin));
        }
        return carToBuyByVin.get();
    }

    @Transactional
    public Optional<CarToBuy> findOptionalCarToBuy(String vin) {
        return carToBuyDAO.findCarToBuyByVin(vin);
    }
    
    @Transactional
    public CarToBuy saveCarToBuy(CarToBuy carToBuy) {
        CarToBuy savedCar = carToBuyDAO.saveCarToBuy(carToBuy);
        log.info("Saved car to buy with VIN: [{}]", savedCar.getVin());
        return savedCar;
    }
    
    @Transactional
    public void deleteCarToBuy(String vin) {
        carToBuyDAO.deleteCarToBuy(vin);
        log.info("Deleted car to buy with VIN: [{}]", vin);
    }

    @Transactional
    public Optional<CarToService> findCarToService(String vin) {
        return carToServiceDAO.findCarToServiceByVin(vin);
    }

    @Transactional
    public CarToService saveCarToService(CarToBuy carToBuy) {
        CarToService carToService = CarToService.builder()
            .vin(carToBuy.getVin())
            .brand(carToBuy.getBrand())
            .model(carToBuy.getModel())
            .year(carToBuy.getYear())
            .build();
        return carToServiceDAO.saveCarToService(carToService);
    }

    @Transactional
    public CarToService saveCarToService(CarToService car) {
        return carToServiceDAO.saveCarToService(car);
    }

    public List<CarToService> findAllCarsWithHistory() {
        List<CarToService> allCars = carToServiceDAO.findAll();
        log.info("Cars to show history: [{}]", allCars.size());
        return allCars;
    }

    public CarHistory findCarHistoryByVin(String carVin) {
        return carToServiceDAO.findCarHistoryByVin(carVin);
    }
}
