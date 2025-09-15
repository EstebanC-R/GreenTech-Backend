package com.api.cruds.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "SENSOR_DATA", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id,timestamp"),
        @Index(name = "idx_user_timestamp", columnList = "user_email,timestamp")
})
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // RELACIÓN CON TU UserModel (usando email como FK)
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "id_usuario", nullable = false)
    private UserModel user;

    @Column(name = "temperatura_ambiente")
    private Float temperaturaAmbiente;

    @Column(name = "humedad_ambiente")
    private Float humedadAmbiente;

    @Column(name = "temperatura_suelo")
    private Float temperaturaSuelo;

    @Column(name = "humedad_suelo")
    private Float humedadSuelo;

    @Column(name = "battery_level")
    private Float batteryLevel;

    @Column(name = "timestamp")
    private Date timestamp;

    // Constructores
    public SensorData() {
        this.timestamp = new Date();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

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