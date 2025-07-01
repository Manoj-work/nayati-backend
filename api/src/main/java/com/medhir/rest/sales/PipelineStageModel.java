package com.medhir.rest.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "pipelineStages")
public class PipelineStageModel {

    @Id
    @JsonIgnore
    private String id; // MongoDB-generated ID

    @Indexed(unique = true)
    private String stageId; // Custom stage ID (Snowflake)

    private String stageName;
    private String color;
    private boolean formRequired;
    private LocalDateTime timestamp;
    private Integer order;
    private List<FormField> formFields;

    @Getter
    @Setter
    public static class FormField {
        private String fieldName;
        private String label;
        private String fieldType;
        private boolean required;
        private String validationRegex;
        private String validationMessage;
    }
}
