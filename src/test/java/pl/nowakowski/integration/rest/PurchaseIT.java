package pl.nowakowski.integration.rest;

import org.junit.jupiter.api.Test;
import pl.nowakowski.api.dto.CarsToBuyDTO;
import pl.nowakowski.api.dto.InvoiceDTO;
import pl.nowakowski.integration.configuration.RestAssuredIntegrationTestBase;
import pl.nowakowski.integration.support.PurchaseControllerTestSupport;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

public class PurchaseIT
        extends RestAssuredIntegrationTestBase
        implements PurchaseControllerTestSupport {

    @Test
    void thatCarPurchaseWorksCorrectly(){
        //given
        CarsToBuyDTO carsToBuyDTO = findAvailableCars();
        CarPurchaseDTO carPurchaseDTO = someCarPurchaseDTO();

        //when
        InvoiceDTO invoiceDTO = purchaseCar(carPurchaseDTO);

        //then
        CarsToBuyDTO carsToBuyDTOAfterPurchase = findAvailableCars();

        assertThat(invoiceDTO.getInvoiceNumber()).isNotNull();
        assertThat(invoiceDTO.getDateTime()).isNotNull();
        var carsToBuyAfterPurchase = new ArrayList<>(carsToBuyDTOAfterPurchase.getCarsToBuy());
        var carsToBuyBeforePurchase = new ArrayList<>(carsToBuyDTO.getCarsToBuy());
        carsToBuyBeforePurchase.removeAll(carsToBuyAfterPurchase);
        assertThat(carsToBuyBeforePurchase).hasSize(1);
        assertThat(carsToBuyBeforePurchase.get(0).getVin()).isEqualTo(carPurchaseDTO.getCarVin());
    }

    public static CarPurchaseDTO someCarPurchaseDTO(){
        return CarPurchaseDTO.buildDefaultData()
                .withCarVin("1FT7X2B60FEA74019")
                .withSalesmanPesel("73021314515");
    }
}
