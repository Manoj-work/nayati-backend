package com.medhir.rest.mapper.accountantModule;

import com.medhir.rest.dto.accountingModule.receipt.ReceiptCreateDTO;
import com.medhir.rest.model.accountantModule.Receipt;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {
    @Mapping(target = "linkedInvoices", ignore = true)
    Receipt toReceipt(ReceiptCreateDTO dto);

}
