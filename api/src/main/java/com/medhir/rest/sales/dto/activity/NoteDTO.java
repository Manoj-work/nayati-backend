package com.medhir.rest.sales.dto.activity;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private String id;
    private String content;
    private String user;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "Timestamp must be in yyyy-MM-dd'T'HH:mm:ss format"
    )
    private String timestamp;
} 