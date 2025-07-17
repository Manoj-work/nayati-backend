package com.medhir.rest.assetManagement.assetSetting;

import com.medhir.rest.assetManagement.assetSetting.model.Category;
import com.medhir.rest.assetManagement.assetSetting.model.Location;
import com.medhir.rest.assetManagement.assetSetting.model.StatusLabel;
import com.medhir.rest.assetManagement.assetSetting.model.CustomField;
import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import com.medhir.rest.assetManagement.assetSetting.dto.CategoryDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.LocationDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.StatusLabelDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.CustomFieldDTO;
import com.medhir.rest.assetManagement.assetSetting.dto.IDFormattingDTO;
import com.medhir.rest.assetManagement.assetSetting.service.AssetIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asset-settings")
public class AssetSettingController {
    
    @Autowired
    private AssetSettingService assetSettingService;
    
    @Autowired
    private AssetIdGeneratorService assetIdGeneratorService;

    // Category endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = assetSettingService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<Category> getCategoryByCategoryId(@PathVariable String categoryId) {
        Category category = assetSettingService.getCategoryByCategoryId(categoryId);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/categories")
    public ResponseEntity<Map<String, String>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        assetSettingService.createCategory(categoryDTO);
        return ResponseEntity.ok(Map.of("message", "Added successfully"));
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<Map<String, String>> updateCategory(@PathVariable String categoryId, @RequestBody CategoryDTO categoryDTO) {
        assetSettingService.updateCategoryByCategoryId(categoryId, categoryDTO);
        return ResponseEntity.ok(Map.of("message", "Updated successfully"));
    }

    @PatchMapping("/categories/batch")
    public ResponseEntity<Map<String, String>> updateCategoriesBatch(@RequestBody List<CategoryDTO> categoryDTOs) {
        assetSettingService.updateCategoriesBatch(categoryDTOs);
        return ResponseEntity.ok(Map.of("message", "Categories updated successfully"));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable String categoryId) {
        assetSettingService.deleteCategoryByCategoryId(categoryId);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // Location endpoints
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = assetSettingService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<Location> getLocationByLocationId(@PathVariable String locationId) {
        Location location = assetSettingService.getLocationByLocationId(locationId);
        return ResponseEntity.ok(location);
    }

    @PostMapping("/locations")
    public ResponseEntity<Map<String, String>> createLocation(@RequestBody LocationDTO locationDTO) {
        assetSettingService.createLocation(locationDTO);
        return ResponseEntity.ok(Map.of("message", "Added successfully"));
    }

    @PatchMapping("/locations/{locationId}")
    public ResponseEntity<Map<String, String>> updateLocation(@PathVariable String locationId, @RequestBody LocationDTO locationDTO) {
        assetSettingService.updateLocationByLocationId(locationId, locationDTO);
        return ResponseEntity.ok(Map.of("message", "Updated successfully"));
    }

    @PatchMapping("/locations/batch")
    public ResponseEntity<Map<String, String>> updateLocationsBatch(@RequestBody List<LocationDTO> locationDTOs) {
        assetSettingService.updateLocationsBatch(locationDTOs);
        return ResponseEntity.ok(Map.of("message", "Locations updated successfully"));
    }

    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Map<String, String>> deleteLocation(@PathVariable String locationId) {
        assetSettingService.deleteLocationByLocationId(locationId);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // Status Label endpoints
    @GetMapping("/status-labels")
    public ResponseEntity<List<StatusLabel>> getAllStatusLabels() {
        List<StatusLabel> statusLabels = assetSettingService.getAllStatusLabels();
        return ResponseEntity.ok(statusLabels);
    }

    @GetMapping("/status-labels/{statusLabelId}")
    public ResponseEntity<StatusLabel> getStatusLabelByStatusLabelId(@PathVariable String statusLabelId) {
        StatusLabel statusLabel = assetSettingService.getStatusLabelByStatusLabelId(statusLabelId);
        return ResponseEntity.ok(statusLabel);
    }

    @PostMapping("/status-labels")
    public ResponseEntity<Map<String, String>> createStatusLabel(@RequestBody StatusLabelDTO statusLabelDTO) {
        assetSettingService.createStatusLabel(statusLabelDTO);
        return ResponseEntity.ok(Map.of("message", "Added successfully"));
    }

    @PatchMapping("/status-labels/{statusLabelId}")
    public ResponseEntity<Map<String, String>> updateStatusLabel(@PathVariable String statusLabelId, @RequestBody StatusLabelDTO statusLabelDTO) {
        assetSettingService.updateStatusLabelByStatusLabelId(statusLabelId, statusLabelDTO);
        return ResponseEntity.ok(Map.of("message", "Updated successfully"));
    }

    @PatchMapping("/status-labels/batch")
    public ResponseEntity<Map<String, String>> updateStatusLabelsBatch(@RequestBody List<StatusLabelDTO> statusLabelDTOs) {
        assetSettingService.updateStatusLabelsBatch(statusLabelDTOs);
        return ResponseEntity.ok(Map.of("message", "Status labels updated successfully"));
    }

    @DeleteMapping("/status-labels/{statusLabelId}")
    public ResponseEntity<Map<String, String>> deleteStatusLabel(@PathVariable String statusLabelId) {
        assetSettingService.deleteStatusLabelByStatusLabelId(statusLabelId);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // Custom Field endpoints
    @GetMapping("/custom-fields")
    public ResponseEntity<List<CustomField>> getAllCustomFields(@RequestParam(required = false) String categoryId) {
        List<CustomField> customFields;
        if (categoryId != null && !categoryId.isEmpty()) {
            customFields = assetSettingService.getCustomFieldsByCategoryId(categoryId);
        } else {
            customFields = assetSettingService.getAllCustomFields();
        }
        return ResponseEntity.ok(customFields);
    }

    @GetMapping("/custom-fields/category/{categoryId}")
    public ResponseEntity<List<CustomField>> getCustomFieldsByCategoryId(@PathVariable String categoryId) {
        List<CustomField> customFields = assetSettingService.getCustomFieldsByCategoryId(categoryId);
        return ResponseEntity.ok(customFields);
    }

    @PostMapping("/custom-fields")
    public ResponseEntity<Map<String, String>> createCustomField(@RequestBody CustomFieldDTO customFieldDTO) {
        assetSettingService.createCustomField(customFieldDTO);
        return ResponseEntity.ok(Map.of("message", "Custom field added successfully"));
    }

    @PatchMapping("/custom-fields/{id}")
    public ResponseEntity<Map<String, String>> updateCustomField(@PathVariable String id, @RequestBody CustomFieldDTO customFieldDTO) {
        assetSettingService.updateCustomField(id, customFieldDTO);
        return ResponseEntity.ok(Map.of("message", "Custom field updated successfully"));
    }

    @PatchMapping("/custom-fields/category/{categoryId}")
    public ResponseEntity<Map<String, String>> updateCustomFieldsBatch(@PathVariable String categoryId, @RequestBody List<CustomFieldDTO> customFieldDTOs) {
        assetSettingService.updateCustomFieldsBatch(categoryId, customFieldDTOs);
        return ResponseEntity.ok(Map.of("message", "Custom fields updated successfully"));
    }

    @DeleteMapping("/custom-fields/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomField(@PathVariable String id) {
        assetSettingService.deleteCustomField(id);
        return ResponseEntity.ok(Map.of("message", "Custom field deleted successfully"));
    }

    // ID Formatting endpoints
    @GetMapping("/id-formatting")
    public ResponseEntity<List<IDFormatting>> getAllIDFormattings() {
        List<IDFormatting> idFormattings = assetSettingService.getAllIDFormattings();
        return ResponseEntity.ok(idFormattings);
    }

    @GetMapping("/id-formatting/category/{categoryId}")
    public ResponseEntity<IDFormatting> getIDFormattingByCategoryId(@PathVariable String categoryId) {
        IDFormatting idFormatting = assetSettingService.getIDFormattingByCategoryId(categoryId);
        return ResponseEntity.ok(idFormatting);
    }

    @GetMapping("/id-formatting/id/{idFormattingId}")
    public ResponseEntity<IDFormatting> getIDFormattingByIdFormattingId(@PathVariable String idFormattingId) {
        IDFormatting idFormatting = assetSettingService.getIDFormattingByIdFormattingId(idFormattingId);
        return ResponseEntity.ok(idFormatting);
    }

    @PostMapping("/id-formatting")
    public ResponseEntity<Map<String, String>> createIDFormatting(@RequestBody IDFormattingDTO idFormattingDTO) {
        assetSettingService.createIDFormatting(idFormattingDTO);
        return ResponseEntity.ok(Map.of("message", "ID formatting created successfully"));
    }

    @PatchMapping("/id-formatting/category/{categoryId}")
    public ResponseEntity<Map<String, String>> updateIDFormatting(@PathVariable String categoryId, @RequestBody IDFormattingDTO idFormattingDTO) {
        assetSettingService.updateIDFormattingByCategoryId(categoryId, idFormattingDTO);
        return ResponseEntity.ok(Map.of("message", "ID formatting updated successfully"));
    }

    @DeleteMapping("/id-formatting/category/{categoryId}")
    public ResponseEntity<Map<String, String>> deleteIDFormatting(@PathVariable String categoryId) {
        assetSettingService.deleteIDFormattingByCategoryId(categoryId);
        return ResponseEntity.ok(Map.of("message", "ID formatting deleted successfully"));
    }

    // Asset ID Generation endpoints
    @GetMapping("/id-formatting/preview/{categoryId}")
    public ResponseEntity<Map<String, String>> previewNextAssetId(@PathVariable String categoryId) {
        String nextAssetId = assetIdGeneratorService.previewNextAssetId(categoryId);
        return ResponseEntity.ok(Map.of("nextAssetId", nextAssetId));
    }
} 