package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.CarServiceRequestDAO;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.infrastructure.database.repository.jpa.CarServiceRequestJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.CarServiceRequestEntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CarServiceRequestRepository implements CarServiceRequestDAO {

    private final CarServiceRequestJpaRepository carServiceRequestJpaRepository;
    private final CarServiceRequestEntityMapper carServiceRequestEntityMapper;

    @Override
    public List<CarServiceRequest> findAvailable() {
        return carServiceRequestJpaRepository.findAllByCompletedDateTimeIsNull().stream()
            .map(carServiceRequestEntityMapper::mapFromEntityWithCar)
            .toList();
    }

    @Override
    public Set<CarServiceRequest> findActiveServiceRequestsByCarVin(String carVin) {
        return carServiceRequestJpaRepository.findActiveServiceRequestsByCarVin(carVin).stream()
            .map(carServiceRequestEntityMapper::mapFromEntity)
            .collect(Collectors.toSet());
    }
    
    @Override
    public Optional<CarServiceRequest> findByServiceRequestNumberWithDetails(String serviceRequestNumber) {
        return carServiceRequestJpaRepository.findByCarServiceRequestNumber(serviceRequestNumber)
            .map(carServiceRequestEntityMapper::mapFromEntityWithDetails);
    }
}
