package com.medhir.rest.accounting;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "bills")
public class BillModel {

    @Id
    private String id;

    private String vendorId; // Reference to Vendor model

    private String billReference;

    private Date billDate;

    private Date dueDate;

    private String placeOfSupply;

    private String gstTreatment;

    private boolean reverseCharge;

    private String company;

    private String journal;

    private String currency;

    private String status;

    private Double totalAmount;

    private List<BillLine> billLines;
}
