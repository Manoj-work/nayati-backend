package com.medhir.rest.sales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {
    @NotBlank(message = "Content is required")
    private String note;
    private String timestamp;

}
