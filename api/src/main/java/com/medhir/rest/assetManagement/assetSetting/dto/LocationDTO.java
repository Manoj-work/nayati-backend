package com.medhir.rest.assetManagement.assetSetting.dto;

public class LocationDTO {
    private String locationId;
    private String name;
    private String address;
    
    public LocationDTO() {}
    
    public LocationDTO(String locationId, String name, String address) {
        this.locationId = locationId;
        this.name = name;
        this.address = address;
    }
    
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
} 