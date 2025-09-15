package com.api.cruds.dto;

public class SensorDataRequest {
    private String deviceCode;
    private Float temperaturaAmbiente;
    private Float humedadAmbiente;
    private Float temperaturaSuelo;
    private Float humedadSuelo;
    private Float batteryLevel;
    private Long timestamp;

    // Constructores
    public SensorDataRequest() {}

    // Getters y Setters
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

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

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }


    @Override
    public String toString() {
        return "SensorDataRequest{" +
                "deviceCode='" + deviceCode + '\'' +
                ", temperaturaAmbiente=" + temperaturaAmbiente +
                ", humedadAmbiente=" + humedadAmbiente +
                ", temperaturaSuelo=" + temperaturaSuelo +
                ", humedadSuelo=" + humedadSuelo +
                ", batteryLevel=" + batteryLevel +
                ", timestamp=" + timestamp +
                '}';
    }
}