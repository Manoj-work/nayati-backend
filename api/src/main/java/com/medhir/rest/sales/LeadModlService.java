package com.medhir.rest.sales;

import com.medhir.rest.sales.dto.LeadDto;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeadModlService {

    @Autowired
    private LeadModlRepository repository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public LeadDto addLead(LeadDto dto) {
        LeadModl entity = toEntity(dto);
        entity.setLeadId("LEAD" + snowflakeIdGenerator.nextId());
        LeadModl saved = repository.save(entity);
        return toDto(saved);
    }

    public List<LeadDto> getAllLeads() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<LeadDto> getLeadById(String id) {
        return repository.findById(id).map(this::toDto);
    }

    public Optional<LeadDto> updateLead(String id, LeadDto dto) {
        return repository.findById(id).map(existing -> {
            LeadModl updated = toEntity(dto);
            updated.setId(id);
            updated.setLeadId(existing.getLeadId());
            LeadModl saved = repository.save(updated);
            return toDto(saved);
        });
    }

    public boolean deleteLead(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Conversion Methods
//    public LeadDto toDto(LeadModl entity) {
//        return new LeadDto(
//                entity.getId(),
//                entity.getLeadId(),
//                entity.getName(),
//                entity.getEmail(),
//                entity.getPhone(),
//                entity.getProjectType(),
//                entity.getPropertyType(),
//                entity.getLocation(),
//                entity.getBudget(),
//                entity.getSource(),
//                entity.getStatus(),
//                entity.getFollowUpDate() != null ? entity.getFollowUpDate().toString() : null,
//                entity.getNotes()
//        );
//    }
    public LeadDto toDto(LeadModl entity) {
        return new LeadDto(
                entity.getId(),
                entity.getLeadId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getProjectType(),
                entity.getPropertyType(),
                entity.getLocation(),
                entity.getBudget(),
                entity.getSource(),
                entity.getStatus(),
                entity.getFollowUpDate(),  // Direct String copy
                entity.getNotes()
        );
    }

    public LeadModl toEntity(LeadDto dto) {
        LeadModl entity = new LeadModl();
        entity.setId(dto.getId());
        entity.setLeadId(dto.getLeadId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setProjectType(dto.getProjectType());
        entity.setPropertyType(dto.getPropertyType());
        entity.setLocation(dto.getLocation());
        entity.setBudget(dto.getBudget());
        entity.setSource(dto.getSource());
        entity.setStatus(dto.getStatus());
        entity.setFollowUpDate(dto.getFollowUpDate());  // Direct String assignment
        entity.setNotes(dto.getNotes());
        return entity;
    }
}

