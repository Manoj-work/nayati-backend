package com.medhir.rest.mapper.accountantModule;

import com.medhir.rest.dto.accountantModule.invoice.InvoiceCreateDTO;
import com.medhir.rest.model.accountantModule.Invoice;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    // Maps Create DTO → Entity
    @Mapping(target = "linkedReceipts", ignore = true)
    Invoice toInvoice(InvoiceCreateDTO request);

//     Optional: If you add UpdateInvoiceRequest later
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateInvoiceFromRequest(UpdateInvoiceRequest request, @MappingTarget Invoice invoice);

}
