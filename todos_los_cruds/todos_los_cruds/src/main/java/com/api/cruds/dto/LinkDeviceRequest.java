package com.api.cruds.dto;

public class LinkDeviceRequest {
    private String deviceCode;
    private String deviceName;

    // Constructores
    public LinkDeviceRequest() {}

    public LinkDeviceRequest(String deviceCode, String deviceName) {
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
    }

    // Getters y Setters
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
}