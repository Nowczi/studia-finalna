package pl.nowakowski.api.dto.mapper;

import org.mapstruct.Mapper;
import pl.nowakowski.api.dto.InvoiceDTO;
import pl.nowakowski.domain.Invoice;

@Mapper(componentModel = "spring")
public interface InvoiceMapper extends OffsetDateTimeMapper {


    InvoiceDTO map(Invoice invoice);
}
