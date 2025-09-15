package com.api.cruds.dto;

import com.api.cruds.models.SensorData;

import java.util.Date;

public class SensorDataResponse {
    private Long id;
    private String deviceCode;
    private String deviceName;
    private Float temperaturaAmbiente;
    private Float humedadAmbiente;
    private Float temperaturaSuelo;
    private Float humedadSuelo;
    private Float batteryLevel;
    private Date timestamp;

    // Constructor desde SensorData entity
    public SensorDataResponse(SensorData sensorData) {
        this.id = sensorData.getId();
        this.deviceCode = sensorData.getDevice().getDeviceCode();
        this.deviceName = sensorData.getDevice().getDeviceName();
        this.temperaturaAmbiente = sensorData.getTemperaturaAmbiente();
        this.humedadAmbiente = sensorData.getHumedadAmbiente();
        this.temperaturaSuelo = sensorData.getTemperaturaSuelo();
        this.humedadSuelo = sensorData.getHumedadSuelo();
        this.batteryLevel = sensorData.getBatteryLevel();
        this.timestamp = sensorData.getTimestamp();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public Float getTemperaturaAmbiente() { return temperaturaAmbiente; }
    public void setTemperaturaAmbiente(Float temperaturaAmbiente) { this.temperaturaAmbiente = temperaturaAmbiente; }

    public Float getHumedadAmbiente() { return humedadAmbiente; }
    public void setHumedadAmbiente(Float humedadAmbiente) { this.humedadAmbiente = humedadAmbiente; }

    public Float getTemperaturaSuelo() { return temperaturaSuelo; }
    public void setTemperaturaSuelo(Float temperaturaSuelo) { this.temperaturaSuelo = temperaturaSuelo; }

    public Float getHumedadSuelo() { return humedadSuelo; }
    public void setHumedadSuelo(Float humedadSuelo) { this.humedadSuelo = humedadSuelo; }

    public Float getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Float batteryLevel) { this.batteryLevel = batteryLevel; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
