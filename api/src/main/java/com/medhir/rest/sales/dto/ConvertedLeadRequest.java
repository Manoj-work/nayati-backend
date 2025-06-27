package com.medhir.rest.sales.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
public class ConvertedLeadRequest {
//    @NotNull(message = "finalQuotation must not be null")
//    @PositiveOrZero(message = "finalQuotation must be zero or positive")
    private BigDecimal initialQuotedAmount;
    private BigDecimal finalQuotation;
    private BigDecimal signUpAmount;
    private String paymentDate;
    private String paymentMode;
    private String panNumber;
    private String projectTimeline;
    private BigDecimal discount;
    private MultipartFile paymentDetailsFile;
    private MultipartFile bookingFormFile;
}
