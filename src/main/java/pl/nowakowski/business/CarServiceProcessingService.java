package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.ServiceRequestProcessingDAO;
import pl.nowakowski.domain.CarServiceProcessingRequest;
import pl.nowakowski.domain.CarServiceRequest;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.domain.Part;
import pl.nowakowski.domain.Service;
import pl.nowakowski.domain.ServiceMechanic;
import pl.nowakowski.domain.ServicePart;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class CarServiceProcessingService {

    private final MechanicService mechanicService;
    private final CarService carService;
    private final ServiceCatalogService serviceCatalogService;
    private final PartCatalogService partCatalogService;
    private final CarServiceRequestService carServiceRequestService;
    private final ServiceRequestProcessingDAO serviceRequestProcessingDAO;

    @Transactional
    public void process(CarServiceProcessingRequest request) {
        Mechanic mechanic = mechanicService.findMechanic(request.getMechanicPesel());
        carService.findCarToService(request.getCarVin()).orElseThrow();
        CarServiceRequest serviceRequest = carServiceRequestService.findAnyActiveServiceRequest(request.getCarVin());

        Service service = serviceCatalogService.findService(request.getServiceCode());

        ServiceMechanic serviceMechanic = buildServiceMechanic(request, mechanic, serviceRequest, service);

        if (request.getDone()) {
            serviceRequest = serviceRequest.withCompletedDateTime(OffsetDateTime.now(ZoneId.of("Europe/Warsaw")));
        }

        if (request.partNotIncluded()) {
            serviceRequestProcessingDAO.process(serviceRequest, serviceMechanic);
        } else {
            Part part = partCatalogService.findPart(request.getPartSerialNumber());
            ServicePart servicePart = buildServicePart(request, serviceRequest, part);
            serviceRequestProcessingDAO.process(serviceRequest, serviceMechanic, servicePart);
        }
    }

    /**
     * Marks a service request as completed WITHOUT creating an additional service_mechanic entry.
     * This method should be called when work has already been recorded (e.g., via process() for parts)
     * and we just need to mark the service request as done.
     *
     * @param carVin the VIN of the car whose active service request should be marked as completed
     */
    @Transactional
    public void completeServiceRequest(String carVin) {
        CarServiceRequest serviceRequest = carServiceRequestService.findAnyActiveServiceRequest(carVin);
        serviceRequest = serviceRequest.withCompletedDateTime(OffsetDateTime.now(ZoneId.of("Europe/Warsaw")));
        serviceRequestProcessingDAO.markServiceRequestAsCompleted(serviceRequest);
    }

    private ServiceMechanic buildServiceMechanic(
        CarServiceProcessingRequest request,
        Mechanic mechanic,
        CarServiceRequest serviceRequest,
        Service service
    ) {
        // If parts are included, use part quantity as service quantity
        // This ensures service appears multiple times when multiple parts are used
        // so customer is charged for each part's associated service
        Integer quantity = request.partNotIncluded() ? 1 : request.getPartQuantity();
        
        return ServiceMechanic.builder()
            .hours(request.getHours())
            .comment(request.getComment())
            .quantity(quantity)
            .carServiceRequest(serviceRequest)
            .mechanic(mechanic)
            .service(service)
            .build();
    }

    private ServicePart buildServicePart(
        CarServiceProcessingRequest request,
        CarServiceRequest serviceRequest,
        Part part
    ) {
        return ServicePart.builder()
            .quantity(request.getPartQuantity())
            .carServiceRequest(serviceRequest)
            .part(part)
            .build();
    }
}
