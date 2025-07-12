package com.medhir.rest.assetManagement.asset.service;

import com.medhir.rest.assetManagement.asset.model.Asset;
import com.medhir.rest.assetManagement.asset.repository.AssetRepository;
import com.medhir.rest.assetManagement.assetSetting.service.AssetIdGeneratorService;
import com.medhir.rest.utils.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private AssetIdGeneratorService assetIdGeneratorService;
    // @Autowired other services for validation (category, location, etc.)

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetById(String id) {
        Optional<Asset> asset = assetRepository.findById(id);
        if (asset.isPresent()) {
            return asset.get();
        }
        throw new RuntimeException("Asset not found with id: " + id);
    }

    public Asset getAssetByAssetId(String assetId) {
        Optional<Asset> asset = assetRepository.findByAssetId(assetId);
        if (asset.isPresent()) {
            return asset.get();
        }
        throw new RuntimeException("Asset not found with assetId: " + assetId);
    }

    public Asset patchAssetByAssetId(String assetId, Asset updatedAsset, MultipartFile invoiceScan) {
        Asset existingAsset = getAssetByAssetId(assetId);
        return patchAssetInternal(existingAsset, updatedAsset, invoiceScan);
    }

    private Asset patchAssetInternal(Asset existingAsset, Asset updatedAsset, MultipartFile invoiceScan) {
        // Update only non-null fields (partial update)
        if (updatedAsset.getName() != null) {
            existingAsset.setName(updatedAsset.getName());
        }
        if (updatedAsset.getCategoryId() != null) {
            existingAsset.setCategoryId(updatedAsset.getCategoryId());
        }
        if (updatedAsset.getSerialNumber() != null) {
            existingAsset.setSerialNumber(updatedAsset.getSerialNumber());
        }
        if (updatedAsset.getLocationId() != null) {
            existingAsset.setLocationId(updatedAsset.getLocationId());
        }
        if (updatedAsset.getStatusLabelId() != null) {
            existingAsset.setStatusLabelId(updatedAsset.getStatusLabelId());
        }
        if (updatedAsset.getAssignedTo() != null) {
            existingAsset.setAssignedTo(updatedAsset.getAssignedTo());
        }
        if (updatedAsset.getPurchaseDate() != null) {
            existingAsset.setPurchaseDate(updatedAsset.getPurchaseDate());
        }
        if (updatedAsset.getPurchaseCost() != null) {
            existingAsset.setPurchaseCost(updatedAsset.getPurchaseCost());
        }
        if (updatedAsset.getVendorId() != null) {
            existingAsset.setVendorId(updatedAsset.getVendorId());
        }
        if (updatedAsset.getInvoiceNumber() != null) {
            existingAsset.setInvoiceNumber(updatedAsset.getInvoiceNumber());
        }
        if (updatedAsset.getWarrantyExpiry() != null) {
            existingAsset.setWarrantyExpiry(updatedAsset.getWarrantyExpiry());
        }
        if (updatedAsset.getCustomFields() != null) {
            existingAsset.setCustomFields(updatedAsset.getCustomFields());
        }
        if (updatedAsset.getGstRate() != null) {
            existingAsset.setGstRate(updatedAsset.getGstRate());
        }
        if (updatedAsset.getInputTaxCreditEligible() != null) {
            existingAsset.setInputTaxCreditEligible(updatedAsset.getInputTaxCreditEligible());
        }
        
        // Handle invoice scan upload
        if (invoiceScan != null && !invoiceScan.isEmpty()) {
            String url = minioService.uploadAssetInvoice(invoiceScan, existingAsset.getVendorId());
            existingAsset.setInvoiceScanUrl(url);
        }
        
        return assetRepository.save(existingAsset);
    }

//    public Asset createAsset(Asset asset, MultipartFile invoiceScan) {
//        // TODO: Validate category, location, etc.
//
//        // Generate asset ID based on category
//        if (asset.getCategoryId() != null) {
//            String generatedAssetId = assetIdGeneratorService.generateAssetId(asset.getCategoryId());
//            asset.setAssetId(generatedAssetId);
//        }
//
//        if (invoiceScan != null && !invoiceScan.isEmpty()) {
//            String url = minioService.uploadAssetInvoice(invoiceScan, asset.getVendorId());
//            asset.setInvoiceScanUrl(url);
//        } else {
//            asset.setInvoiceScanUrl(null);
//        }
//        return assetRepository.save(asset);
//    }
public Asset createAsset(Asset asset, MultipartFile invoiceScan) {
    // Validate and generate asset ID based on category
    if (asset.getCategoryId() != null) {
        String generatedAssetId = assetIdGeneratorService.generateAssetId(asset.getCategoryId());
        asset.setAssetId(generatedAssetId);
    }

    // Handle invoice scan upload
    if (invoiceScan != null && !invoiceScan.isEmpty()) {
        String url = minioService.uploadAssetInvoice(invoiceScan, asset.getVendorId());  // Upload and get URL
        System.out.println("url " + url);
        asset.setInvoiceScanUrl(url);
    } else {
        asset.setInvoiceScanUrl(null);
        // Optional: make it null if no file uploaded
    }

    return assetRepository.save(asset);
}


    public Asset patchAsset(String id, Asset updatedAsset, MultipartFile invoiceScan) {
        Asset existingAsset = getAssetById(id);
        return patchAssetInternal(existingAsset, updatedAsset, invoiceScan);
    }

    public Asset updateAsset(String id, Asset updatedAsset, MultipartFile invoiceScan) {
        Asset existingAsset = getAssetById(id);
        
        // Update all fields (complete replacement)
        existingAsset.setName(updatedAsset.getName());
        existingAsset.setCategoryId(updatedAsset.getCategoryId());
        existingAsset.setSerialNumber(updatedAsset.getSerialNumber());
        existingAsset.setLocationId(updatedAsset.getLocationId());
        existingAsset.setStatusLabelId(updatedAsset.getStatusLabelId());
        existingAsset.setAssignedTo(updatedAsset.getAssignedTo());
        existingAsset.setPurchaseDate(updatedAsset.getPurchaseDate());
        existingAsset.setPurchaseCost(updatedAsset.getPurchaseCost());
        existingAsset.setVendorId(updatedAsset.getVendorId());
        existingAsset.setInvoiceNumber(updatedAsset.getInvoiceNumber());
        existingAsset.setWarrantyExpiry(updatedAsset.getWarrantyExpiry());
        existingAsset.setCustomFields(updatedAsset.getCustomFields());
        existingAsset.setGstRate(updatedAsset.getGstRate());
        existingAsset.setInputTaxCreditEligible(updatedAsset.getInputTaxCreditEligible());
        
        // Handle invoice scan upload
        if (invoiceScan != null && !invoiceScan.isEmpty()) {
            String url = minioService.uploadAssetInvoice(invoiceScan, existingAsset.getVendorId());
            existingAsset.setInvoiceScanUrl(url);
        }
        
        return assetRepository.save(existingAsset);
    }

    public void deleteAsset(String id) {
        Asset asset = getAssetById(id); // This will throw exception if not found
        assetRepository.delete(asset);
    }
} 