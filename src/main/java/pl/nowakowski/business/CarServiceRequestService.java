package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.CarServiceRequestDAO;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.CarToBuy;
import pl.nowakowski.domain.CarToService;
import pl.nowakowski.domain.Customer;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.domain.exception.NotFoundException;
import pl.nowakowski.domain.exception.ProcessingException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@AllArgsConstructor
public class CarServiceRequestService {

    private final MechanicService mechanicService;
    private final CarService carService;
    private final CustomerService customerService;
    private final CarServiceRequestDAO carServiceRequestDAO;

    public List<Mechanic> availableMechanics() {
        return mechanicService.findAvailable();
    }

    public List<CarServiceRequest> availableServiceRequests() {
        return carServiceRequestDAO.findAvailable();
    }
    
    /**
     * Finds a service request by its number with all details including service mechanics and parts.
     * @param serviceRequestNumber the service request number
     * @return Optional containing the service request with all details
     */
    @Transactional
    public Optional<CarServiceRequest> findServiceRequestByNumberWithDetails(String serviceRequestNumber) {
        return carServiceRequestDAO.findByServiceRequestNumberWithDetails(serviceRequestNumber);
    }

    @Transactional
    public void makeServiceRequest(CarServiceRequest serviceRequest) {
        // Check if this is an existing customer (name is null) or new customer (name is provided)
        if (serviceRequest.getCustomer().getName() == null) {
            saveServiceRequestForExistingCar(serviceRequest);
        } else {
            saveServiceRequestForNewCar(serviceRequest);
        }
    }

    private void saveServiceRequestForExistingCar(CarServiceRequest request) {
        validate(request.getCar().getVin());

        CarToService car = carService.findCarToService(request.getCar().getVin())
            .orElseGet(() -> findInCarToBuyOrCreateNewCarToService(request.getCar()));
        Customer customer = customerService.findCustomer(request.getCustomer().getEmail());

        CarServiceRequest carServiceRequest = buildCarServiceRequest(request, car, customer);
        Set<CarServiceRequest> existingCarServiceRequests = customer.getCarServiceRequests();
        existingCarServiceRequests.add(carServiceRequest);
        customerService.saveServiceRequest(customer.withCarServiceRequests(existingCarServiceRequests));
    }

    private void saveServiceRequestForNewCar(CarServiceRequest request) {
        validate(request.getCar().getVin());

        CarToService car = carService.saveCarToService(request.getCar());
        Customer customer = customerService.saveCustomer(request.getCustomer());

        CarServiceRequest carServiceRequest = buildCarServiceRequest(request, car, customer);
        Set<CarServiceRequest> existingCarServiceRequests = customer.getCarServiceRequests();
        existingCarServiceRequests.add(carServiceRequest);
        customerService.saveServiceRequest(customer.withCarServiceRequests(existingCarServiceRequests));
    }

    private void validate(String carVin) {
        Set<CarServiceRequest> serviceRequests = carServiceRequestDAO.findActiveServiceRequestsByCarVin(carVin);
        if (serviceRequests.size() == 1) {
            throw new ProcessingException(
                "There should be only one active service request at a time, car vin: [%s]".formatted(carVin)
            );
        }
    }

    private CarToService findInCarToBuyOrCreateNewCarToService(CarToService car) {
        Optional<CarToBuy> carToBuy = carService.findOptionalCarToBuy(car.getVin());
        if (carToBuy.isPresent()) {
            return carService.saveCarToService(carToBuy.get());
        } else {
            // Car not found in car_to_buy, create new car in car_to_service with provided details
            if (car.getBrand() == null || car.getModel() == null || car.getYear() == null) {
                throw new NotFoundException(
                    "Could not find car by vin: [%s]. Please provide car details (brand, model, year).".formatted(car.getVin())
                );
            }
            return carService.saveCarToService(car);
        }
    }

    private CarServiceRequest buildCarServiceRequest(
        CarServiceRequest request,
        CarToService car,
        Customer customer
    ) {
        OffsetDateTime when = OffsetDateTime.now(ZoneId.of("Europe/Warsaw"));
        return CarServiceRequest.builder()
            .carServiceRequestNumber(generateCarServiceRequestNumber(when))
            .receivedDateTime(when)
            .customerComment(request.getCustomerComment())
            .customer(customer)
            .car(car)
            .build();
    }

    private String generateCarServiceRequestNumber(OffsetDateTime when) {
        return "%s.%s.%s-%s.%s.%s.%s".formatted(
            when.getYear(),
            when.getMonthValue(),
            when.getDayOfMonth(),
            when.getHour(),
            when.getMinute(),
            when.getSecond(),
            randomInt(10, 100)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    @Transactional
    public CarServiceRequest findAnyActiveServiceRequest(String carVin) {
        Set<CarServiceRequest> serviceRequests = carServiceRequestDAO.findActiveServiceRequestsByCarVin(carVin);
        if (serviceRequests.size() != 1) {
            throw new ProcessingException(
                "There should be only one active service request at a time, car vin: [%s]".formatted(carVin));
        }
        return serviceRequests.stream()
            .findAny()
            .orElseThrow(() -> new NotFoundException("Could not find any service requests, car vin: [%s]".formatted(carVin)));
    }
}
