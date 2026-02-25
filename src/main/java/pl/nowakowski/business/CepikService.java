package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.nowakowski.business.dao.CepikVehicleDAO;
import pl.nowakowski.domain.CepikVehicle;
import pl.nowakowski.domain.exception.NotFoundException;
import pl.nowakowski.domain.exception.ProcessingException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class CepikService {

    private final CepikVehicleDAO cepikVehicleDAO;

    public CepikVehicle findRandom(final LocalDate dateFrom, final LocalDate dateTo){
        log.debug("Looking for random CEPIK vehicle, first registration between: [{}] and: [{}]", dateFrom, dateTo);
        List<CepikVehicle> cepikVehicles = cepikVehicleDAO.getCepikVehicles(dateFrom, dateTo);

        if (cepikVehicles.isEmpty()){
            throw new ProcessingException(
                    "CEPIK returned empty list for first registration between: [%s] and [%s]"
                            .formatted(dateFrom, dateTo)
            );
        }

        return Optional.ofNullable(cepikVehicles.get(new Random().nextInt(cepikVehicles.size())))
                .map(anyVehicle -> cepikVehicleDAO.getCepikVehicle(anyVehicle.getCepikId()))
                .orElseThrow(() -> new NotFoundException("Could not find random CEPIK vehicle"));
    }
}
