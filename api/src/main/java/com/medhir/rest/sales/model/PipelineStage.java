package com.medhir.rest.sales.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pipeline_stages")
public class PipelineStage {
    @Id
    @JsonIgnore
    private String id; // MongoDB ObjectId, not used in business logic

    @Indexed(unique = true)
    private String stageId; // Snowflake-generated Stage ID, used everywhere else
    private String name;
    private String description;
    private int orderIndex;
    private String color;
    private boolean isActive;
    private String createdBy;
    private String createdAt;
    private String updatedAt;
    
    // Constructor for easy creation
    public PipelineStage(String name, String description, int orderIndex, String color, String createdBy) {
        this.name = name;
        this.description = description;
        this.orderIndex = orderIndex;
        this.color = color;
        this.isActive = true;
        this.createdBy = createdBy;
        this.createdAt = java.time.LocalDateTime.now().toString();
        this.updatedAt = java.time.LocalDateTime.now().toString();
    }
} 