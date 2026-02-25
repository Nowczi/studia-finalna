package pl.nowakowski.business.dao;

import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.ServiceMechanic;
import pl.nowakowski.domain.ServicePart;

public interface ServiceRequestProcessingDAO {
    void process(CarServiceRequest serviceRequest, ServiceMechanic serviceMechanic);

    void process(CarServiceRequest serviceRequest, ServiceMechanic serviceMechanic, ServicePart servicePart);

    /**
     * Marks a service request as completed by updating its completedDateTime.
     * This method does NOT create any service_mechanic or service_part entries.
     *
     * @param serviceRequest the service request to mark as completed
     */
    void markServiceRequestAsCompleted(CarServiceRequest serviceRequest);
}
