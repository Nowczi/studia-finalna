package pl.nowakowski.integration.rest;

import org.junit.jupiter.api.Test;
import pl.nowakowski.api.dto.CepikVehicleDTO;
import pl.nowakowski.integration.configuration.RestAssuredIntegrationTestBase;
import pl.nowakowski.integration.support.CepikControllerTestSupport;
import pl.nowakowski.integration.support.WiremockTestSupport;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CepikIT
        extends RestAssuredIntegrationTestBase
        implements CepikControllerTestSupport, WiremockTestSupport {

    @Test
    void thatFindingRandomVehicleWorksCorrectly(){
        //given
        LocalDate dateFrom = LocalDate.of(2022,1,1);
        LocalDate dateTo = LocalDate.of(2022,6,30);
        stubForSlowniki(wireMockServer);
        stubForPojazdy(wireMockServer, dateFrom.toString(), dateTo.toString());
        stubForPojazd(wireMockServer);

        //when
        CepikVehicleDTO randomVehicle1 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle2 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle3 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle4 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle5 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle6 = getCepikRandomVehicle(dateFrom, dateTo);
        CepikVehicleDTO randomVehicle7 = getCepikRandomVehicle(dateFrom, dateTo);

        //Set zeby sprawdzic unikalnosc losowych wynikow
        Set<CepikVehicleDTO> randomVehicles = new HashSet<>();
                randomVehicles.add(randomVehicle1);
                randomVehicles.add(randomVehicle2);
                randomVehicles.add(randomVehicle3);
                randomVehicles.add(randomVehicle4);
                randomVehicles.add(randomVehicle5);
                randomVehicles.add(randomVehicle6);
                randomVehicles.add(randomVehicle7);
        assertThat(randomVehicles).hasSizeGreaterThan(1);

    }


}
