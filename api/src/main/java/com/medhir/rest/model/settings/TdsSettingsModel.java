package com.medhir.rest.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tds_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TdsSettingsModel {

    @Id
    @JsonIgnore
    private String id;

    @NotBlank(message = "Company Id cannot be empty")
    private String companyId;
    private String tdsSettingId = "TDS001";
    private Double tdsRate;
    private String description;
    private String createdAt;
    private String updatedAt;
}