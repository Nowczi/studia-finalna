package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.nowakowski.api.dto.CarHistoryDTO;
import pl.nowakowski.api.dto.CarToBuyDTO;
import pl.nowakowski.api.dto.CarToServiceDTO;
import pl.nowakowski.domain.CarHistory;
import pl.nowakowski.domain.CarToBuy;
import pl.nowakowski.domain.CarToService;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper extends OffsetDateTimeMapper {

    CarToBuyDTO map(final CarToBuy car);
    
    // Reverse mapping from DTO to domain
    @Mapping(target = "carToBuyId", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    CarToBuy map(final CarToBuyDTO carDTO);

    CarToServiceDTO map(final CarToService car);

    @Mapping(source = "carServiceRequests", target = "carServiceRequests", qualifiedByName = "mapServiceRequests")
    CarHistoryDTO map(CarHistory carHistory);

    @SuppressWarnings("unused")
    @Named("mapServiceRequests")
    default List<CarHistoryDTO.ServiceRequestDTO> mapServiceRequests(
        List<CarHistory.CarServiceRequest> requests
    ) {
        return requests.stream().map(this::mapServiceRequest).toList();
    }

    @Mapping(source = "receivedDateTime", target = "receivedDateTime", qualifiedByName = "mapOffsetDateTimeToString")
    @Mapping(source = "completedDateTime", target = "completedDateTime", qualifiedByName = "mapOffsetDateTimeToString")
    @Mapping(source = "serviceWorks", target = "serviceWorks", qualifiedByName = "mapServiceWorks")
    CarHistoryDTO.ServiceRequestDTO mapServiceRequest(CarHistory.CarServiceRequest carServiceRequest);
    
    @SuppressWarnings("unused")
    @Named("mapServiceWorks")
    default List<CarHistoryDTO.ServiceWorkDTO> mapServiceWorks(
        List<CarHistory.ServiceWork> serviceWorks
    ) {
        if (serviceWorks == null) {
            return null;
        }
        return serviceWorks.stream().map(this::mapServiceWork).toList();
    }
    
    CarHistoryDTO.ServiceWorkDTO mapServiceWork(CarHistory.ServiceWork serviceWork);
}
