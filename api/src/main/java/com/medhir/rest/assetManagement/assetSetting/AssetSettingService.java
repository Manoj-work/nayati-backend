package com.medhir.rest.assetManagement.assetSetting;

import com.medhir.rest.assetManagement.assetSetting.model.Category;
import com.medhir.rest.assetManagement.assetSetting.model.Location;
import com.medhir.rest.assetManagement.assetSetting.model.StatusLabel;
import com.medhir.rest.assetManagement.assetSetting.model.CustomField;
import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import com.medhir.rest.assetManagement.assetSetting.repository.CategoryRepository;
import com.medhir.rest.assetManagement.assetSetting.repository.LocationRepository;
import com.medhir.rest.assetManagement.assetSetting.repository.StatusLabelRepository;
import com.medhir.rest.assetManagement.assetSetting.repository.CustomFieldRepository;
import com.medhir.rest.assetManagement.assetSetting.repository.IDFormattingRepository;
import com.medhir.rest.assetManagement.assetSetting.mapper.CategoryMapper;
import com.medhir.rest.assetManagement.assetSetting.mapper.LocationMapper;
import com.medhir.rest.assetManagement.assetSetting.mapper.StatusLabelMapper;
import com.medhir.rest.assetManagement.assetSetting.mapper.CustomFieldMapper;
import com.medhir.rest.assetManagement.assetSetting.mapper.IDFormattingMapper;
import com.medhir.rest.assetManagement.assetSetting.dto.CategoryDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.LocationDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.StatusLabelDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.CustomFieldDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.IDFormattingDTO;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class AssetSettingService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private StatusLabelRepository statusLabelRepository;
    
    @Autowired
    private CustomFieldRepository customFieldRepository;
    
    @Autowired
    private IDFormattingRepository idFormattingRepository;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private LocationMapper locationMapper;
    
    @Autowired
    private StatusLabelMapper statusLabelMapper;
    
    @Autowired
    private CustomFieldMapper customFieldMapper;
    
    @Autowired
    private IDFormattingMapper idFormattingMapper;
    
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    // Category methods
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryByCategoryId(String categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with categoryId: " + categoryId));
    }

    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setId(String.valueOf(snowflakeIdGenerator.nextId()));
        category.setCategoryId("CAT-" + snowflakeIdGenerator.nextId());
        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryMapper.updateEntityFromDTO(categoryDTO, category);
        return categoryRepository.save(category);
    }

    public Category updateCategoryByCategoryId(String categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryId(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found with categoryId: " + categoryId));
        categoryMapper.updateEntityFromDTO(categoryDTO, category);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public void deleteCategoryByCategoryId(String categoryId) {
        categoryRepository.deleteByCategoryId(categoryId);
    }

    public void updateCategoriesBatch(List<CategoryDTO> categoryDTOs) {
        for (CategoryDTO dto : categoryDTOs) {
            Category category = categoryRepository.findByCategoryId(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId()));
            categoryMapper.updateEntityFromDTO(dto, category);
            categoryRepository.save(category);
        }
    }

    // Location methods
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationByLocationId(String locationId) {
        return locationRepository.findByLocationId(locationId)
            .orElseThrow(() -> new RuntimeException("Location not found with locationId: " + locationId));
    }

    public Location createLocation(LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);
        location.setId(String.valueOf(snowflakeIdGenerator.nextId()));
        location.setLocationId("LOC-" + snowflakeIdGenerator.nextId());
        return locationRepository.save(location);
    }

    public Location updateLocation(String id, LocationDTO locationDTO) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found"));
        locationMapper.updateEntityFromDTO(locationDTO, location);
        return locationRepository.save(location);
    }

    public Location updateLocationByLocationId(String locationId, LocationDTO locationDTO) {
        Location location = locationRepository.findByLocationId(locationId)
            .orElseThrow(() -> new RuntimeException("Location not found with locationId: " + locationId));
        locationMapper.updateEntityFromDTO(locationDTO, location);
        return locationRepository.save(location);
    }

    public void deleteLocation(String id) {
        locationRepository.deleteById(id);
    }

    public void deleteLocationByLocationId(String locationId) {
        locationRepository.deleteByLocationId(locationId);
    }

    public void updateLocationsBatch(List<LocationDTO> locationDTOs) {
        for (LocationDTO dto : locationDTOs) {
            Location location = locationRepository.findByLocationId(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found: " + dto.getLocationId()));
            locationMapper.updateEntityFromDTO(dto, location);
            locationRepository.save(location);
        }
    }

    // Status Label methods
    public List<StatusLabel> getAllStatusLabels() {
        return statusLabelRepository.findAll();
    }

    public StatusLabel getStatusLabelByStatusLabelId(String statusLabelId) {
        return statusLabelRepository.findByStatusLabelId(statusLabelId)
            .orElseThrow(() -> new RuntimeException("Status Label not found with statusLabelId: " + statusLabelId));
    }

    public StatusLabel createStatusLabel(StatusLabelDTO statusLabelDTO) {
        StatusLabel statusLabel = statusLabelMapper.toEntity(statusLabelDTO);
        statusLabel.setId(String.valueOf(snowflakeIdGenerator.nextId()));
        statusLabel.setStatusLabelId("STL-" + snowflakeIdGenerator.nextId());
        return statusLabelRepository.save(statusLabel);
    }

    public StatusLabel updateStatusLabel(String id, StatusLabelDTO statusLabelDTO) {
        StatusLabel statusLabel = statusLabelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Status Label not found"));
        statusLabelMapper.updateEntityFromDTO(statusLabelDTO, statusLabel);
        return statusLabelRepository.save(statusLabel);
    }

    public StatusLabel updateStatusLabelByStatusLabelId(String statusLabelId, StatusLabelDTO statusLabelDTO) {
        StatusLabel statusLabel = statusLabelRepository.findByStatusLabelId(statusLabelId)
            .orElseThrow(() -> new RuntimeException("Status Label not found with statusLabelId: " + statusLabelId));
        statusLabelMapper.updateEntityFromDTO(statusLabelDTO, statusLabel);
        return statusLabelRepository.save(statusLabel);
    }

    public void deleteStatusLabel(String id) {
        statusLabelRepository.deleteById(id);
    }

    public void deleteStatusLabelByStatusLabelId(String statusLabelId) {
        statusLabelRepository.deleteByStatusLabelId(statusLabelId);
    }

    public void updateStatusLabelsBatch(List<StatusLabelDTO> statusLabelDTOs) {
        for (StatusLabelDTO dto : statusLabelDTOs) {
            StatusLabel statusLabel = statusLabelRepository.findByStatusLabelId(dto.getStatusLabelId())
                .orElseThrow(() -> new RuntimeException("Status label not found: " + dto.getStatusLabelId()));
            statusLabelMapper.updateEntityFromDTO(dto, statusLabel);
            statusLabelRepository.save(statusLabel);
        }
    }

    // Custom Field methods
    public List<CustomField> getAllCustomFields() {
        return customFieldRepository.findAll();
    }
    
    public List<CustomField> getCustomFieldsByCategoryId(String categoryId) {
        return customFieldRepository.findByCategoryId(categoryId);
    }

    public CustomField createCustomField(CustomFieldDTO customFieldDTO) {
        CustomField customField = customFieldMapper.toEntity(customFieldDTO);
        customField.setId(String.valueOf(snowflakeIdGenerator.nextId()));
        return customFieldRepository.save(customField);
    }

    public CustomField updateCustomField(String id, CustomFieldDTO customFieldDTO) {
        CustomField customField = customFieldRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custom Field not found"));
        customFieldMapper.updateEntityFromDTO(customFieldDTO, customField);
        return customFieldRepository.save(customField);
    }

    public void updateCustomFieldsBatch(String categoryId, List<CustomFieldDTO> customFieldDTOs) {
        for (CustomFieldDTO dto : customFieldDTOs) {
            if (dto.getId() != null && !dto.getId().isEmpty()) {
                // Update existing field
                CustomField customField = customFieldRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Custom Field not found: " + dto.getId()));
                customFieldMapper.updateEntityFromDTO(dto, customField);
                customFieldRepository.save(customField);
            } else {
                // Create new field
                CustomField customField = customFieldMapper.toEntity(dto);
                customField.setId(String.valueOf(snowflakeIdGenerator.nextId()));
                customField.setCategoryId(categoryId); // Ensure categoryId is set
                customFieldRepository.save(customField);
            }
        }
    }

    public void deleteCustomField(String id) {
        customFieldRepository.deleteById(id);
    }

    // ID Formatting methods
    public List<IDFormatting> getAllIDFormattings() {
        return idFormattingRepository.findAll();
    }
    
    public IDFormatting getIDFormattingByCategoryId(String categoryId) {
        return idFormattingRepository.findByCategoryId(categoryId)
            .orElseThrow(() -> new RuntimeException("ID Formatting not found for category: " + categoryId));
    }
    
    public IDFormatting getIDFormattingByIdFormattingId(String idFormattingId) {
        return idFormattingRepository.findByIdFormattingId(idFormattingId)
            .orElseThrow(() -> new RuntimeException("ID Formatting not found with ID: " + idFormattingId));
    }

    public IDFormatting createIDFormatting(IDFormattingDTO idFormattingDTO) {
        IDFormatting idFormatting = idFormattingMapper.toEntity(idFormattingDTO);
        // Don't set MongoDB _id - let MongoDB generate ObjectId automatically
        // Generate business ID using Snowflake
        String snowflakeId = String.valueOf(snowflakeIdGenerator.nextId());
        idFormatting.setIdFormattingId("IDF-" + snowflakeId);
        return idFormattingRepository.save(idFormatting);
    }

    public IDFormatting updateIDFormattingByCategoryId(String categoryId, IDFormattingDTO idFormattingDTO) {
        IDFormatting idFormatting = idFormattingRepository.findByCategoryId(categoryId)
            .orElseThrow(() -> new RuntimeException("ID Formatting not found for category: " + categoryId));
        idFormattingMapper.updateEntityFromDTO(idFormattingDTO, idFormatting);
        return idFormattingRepository.save(idFormatting);
    }
    
    public void deleteIDFormattingByCategoryId(String categoryId) {
        idFormattingRepository.deleteByCategoryId(categoryId);
    }
} 