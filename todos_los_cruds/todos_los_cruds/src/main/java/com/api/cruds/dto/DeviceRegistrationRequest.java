package com.api.cruds.dto;

public class DeviceRegistrationRequest {
    private String deviceCode;
    private String macAddress;
    private String chipId;
    private String deviceName;

    // Constructores
    public DeviceRegistrationRequest() {}

    public DeviceRegistrationRequest(String deviceCode, String macAddress, String chipId, String deviceName) {
        this.deviceCode = deviceCode;
        this.macAddress = macAddress;
        this.chipId = chipId;
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
