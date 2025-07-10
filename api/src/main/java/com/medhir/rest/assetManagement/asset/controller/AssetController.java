package com.medhir.rest.assetManagement.asset.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.assetManagement.asset.model.Asset;
import com.medhir.rest.assetManagement.asset.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    @Autowired
    private AssetService assetService;

    /**
     * Get all assets
     * @return List of all assets
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get asset by MongoDB ID
     * @param id MongoDB Asset ID
     * @return Asset details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String id) {
        Asset asset = assetService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    /**
     * Get asset by Asset ID (auto-generated ID like D-03-3001)
     * @param assetId Asset ID (e.g., D-03-3001)
     * @return Asset details
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Asset> getAssetByAssetId(@PathVariable String assetId) {
        Asset asset = assetService.getAssetByAssetId(assetId);
        return ResponseEntity.ok(asset);
    }

    /**
     * Partially update asset by Asset ID (PATCH) - JSON only
     * @param assetId Asset ID (e.g., D-03-3001)
     * @param asset Updated asset data (only fields to update)
     * @return ResponseEntity with success message
     */
    @PatchMapping(value = "/asset/{assetId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> patchAssetByAssetId(
            @PathVariable String assetId,
            @RequestBody Asset asset) {
        Asset updated = assetService.patchAssetByAssetId(assetId, asset, null);
        return ResponseEntity.ok(Map.of(
                "message", "Asset updated successfully",
                "assetId", updated.getAssetId(),
                "asset", updated
        ));
    }

    /**
     * Create a new asset with optional invoice scan upload.
     * @param assetJson JSON string representing the asset
     * @param invoiceScan Multipart file for invoice scan
     * @return ResponseEntity with message and assetId
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createAsset(
            @RequestPart("asset") String assetJson,
            @RequestPart(value = "invoiceScan", required = false) MultipartFile invoiceScan) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Asset asset = mapper.readValue(assetJson, Asset.class);
        Asset saved = assetService.createAsset(asset, invoiceScan);
        return ResponseEntity.ok(Map.of(
                "message", "Asset created successfully",
                "assetId", saved.getId()
        ));
    }

    /**
     * Partially update an existing asset (PATCH) - JSON only
     * @param id Asset ID
     * @param asset Updated asset data (only fields to update)
     * @return ResponseEntity with success message
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> patchAssetJson(
            @PathVariable String id,
            @RequestBody Asset asset) {
        Asset updated = assetService.patchAsset(id, asset, null);
        return ResponseEntity.ok(Map.of(
                "message", "Asset updated successfully",
                "assetId", updated.getId(),
                "asset", updated
        ));
    }

    /**
     * Partially update an existing asset (PATCH) - With file upload
     * @param id Asset ID
     * @param assetJson Updated asset data (only fields to update)
     * @param invoiceScan Optional new invoice scan
     * @return ResponseEntity with success message
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> patchAssetWithFile(
            @PathVariable String id,
            @RequestPart("asset") String assetJson,
            @RequestPart(value = "invoiceScan", required = false) MultipartFile invoiceScan) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Asset asset = mapper.readValue(assetJson, Asset.class);
        Asset updated = assetService.patchAsset(id, asset, invoiceScan);
        return ResponseEntity.ok(Map.of(
                "message", "Asset updated successfully",
                "assetId", updated.getId(),
                "asset", updated
        ));
    }

    /**
     * Completely replace an existing asset (PUT)
     * @param id Asset ID
     * @param assetJson Complete asset data
     * @param invoiceScan Optional new invoice scan
     * @return ResponseEntity with success message
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateAsset(
            @PathVariable String id,
            @RequestPart("asset") String assetJson,
            @RequestPart(value = "invoiceScan", required = false) MultipartFile invoiceScan) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Asset asset = mapper.readValue(assetJson, Asset.class);
        Asset updated = assetService.updateAsset(id, asset, invoiceScan);
        return ResponseEntity.ok(Map.of(
                "message", "Asset replaced successfully",
                "assetId", updated.getId(),
                "asset", updated
        ));
    }

    /**
     * Delete an asset
     * @param id Asset ID
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(Map.of("message", "Asset deleted successfully"));
    }
} 