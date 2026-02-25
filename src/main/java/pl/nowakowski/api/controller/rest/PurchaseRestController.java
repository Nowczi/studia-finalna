package pl.nowakowski.api.controller.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.nowakowski.api.dto.CarPurchaseDTO;
import pl.nowakowski.api.dto.CarsToBuyDTO;
import pl.nowakowski.api.dto.InvoiceDTO;
import pl.nowakowski.api.dto.mapper.CarPurchaseMapper;
import pl.nowakowski.api.dto.mapper.InvoiceMapper;
import pl.nowakowski.business.CarPurchaseService;
import pl.nowakowski.domain.CarPurchaseRequest;
import pl.nowakowski.domain.Invoice;

@RestController
@AllArgsConstructor
@RequestMapping(PurchaseRestController.API_PURCHASE)
public class PurchaseRestController {

    public static final String API_PURCHASE = "/api/purchase";

    private final CarPurchaseService carPurchaseService;
    private final CarPurchaseMapper carPurchaseMapper;
    private final InvoiceMapper invoiceMapper;

    @GetMapping
    public CarsToBuyDTO carsPurchaseData(){
        return  CarsToBuyDTO.builder()
                .carsToBuy(carPurchaseService.availableCars().stream()
                        .map(carPurchaseMapper::map)
                        .toList())
                .build();

    }

    @PostMapping
    public InvoiceDTO makePurchase(
            @Valid @RequestBody CarPurchaseDTO carPurchaseDTO
            ){
        CarPurchaseRequest request = carPurchaseMapper.map(carPurchaseDTO);
        Invoice invoice = carPurchaseService.purchase(request);

        return invoiceMapper.map(invoice);

    }
}
