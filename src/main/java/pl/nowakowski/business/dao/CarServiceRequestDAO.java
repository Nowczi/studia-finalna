package pl.nowakowski.business.dao;

import pl.nowakowski.domain.CarServiceRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CarServiceRequestDAO {
    List<CarServiceRequest> findAvailable();

    Set<CarServiceRequest> findActiveServiceRequestsByCarVin(String carVin);
    
    /**
     * Finds a service request by its number with all details including service mechanics and parts.
     * @param serviceRequestNumber the service request number
     * @return Optional containing the service request with all details
     */
    Optional<CarServiceRequest> findByServiceRequestNumberWithDetails(String serviceRequestNumber);
}
