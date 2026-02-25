package pl.nowakowski.domain;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
@Builder
@ToString(of = "carVin")
public class CarHistory {

    String carVin;
    List<CarServiceRequest> carServiceRequests;

    @Value
    @Builder
    @ToString(of = {"carServiceRequestNumber", "receivedDateTime", "completedDateTime", "customerComment"})
    public static class CarServiceRequest {
        String carServiceRequestNumber;
        OffsetDateTime receivedDateTime;
        OffsetDateTime completedDateTime;
        String customerComment;
        // Changed from List<Service> to List<ServiceWork> to include mechanic details
        List<ServiceWork> serviceWorks;
        List<Part> parts;
    }
    
    /**
     * Represents a service performed by a mechanic, including their comment and hours.
     * This replaces the simple Service list to avoid duplication and show mechanic details.
     */
    @Value
    @Builder
    public static class ServiceWork {
        String serviceCode;
        String description;
        java.math.BigDecimal price;
        String mechanicName;
        String mechanicSurname;
        Integer hours;
        String mechanicComment;
    }

}
