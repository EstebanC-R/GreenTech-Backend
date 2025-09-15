package com.api.cruds.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "DEVICES")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 8)
    private String deviceCode;

    @Column(unique = true)
    private String macAddress;

    @Column
    private String chipId;

    // RELACIÓN CON TU UserModel (usando email como FK)
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "id_usuario")
    private UserModel user;  // null si no está vinculado

    @Column
    private Date registeredAt;

    @Column
    private Date lastSeen;

    @Column
    private Boolean active = true;

    @Column
    private String deviceName; // Nombre personalizado del usuario

    @Column
    private Float batteryLevel;

    // Constructores
    public Device() {
        this.registeredAt = new Date();
        this.active = true;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getChipId() { return chipId; }
    public void setChipId(String chipId) { this.chipId = chipId; }

    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }

    public Date getLastSeen() { return lastSeen; }
    public void setLastSeen(Date lastSeen) { this.lastSeen = lastSeen; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public Float getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Float batteryLevel) { this.batteryLevel = batteryLevel; }
}
