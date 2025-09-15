package com.api.cruds.dto;

import java.util.Date;

public class DeviceStatusResponse {
    private boolean linked;
    private String userToken;
    private String userName;
    private Date lastSeen;
    private Float batteryLevel;
    private Boolean active;

    // Constructores
    public DeviceStatusResponse() {}

    // Getters y Setters
    public boolean isLinked() { return linked; }
    public void setLinked(boolean linked) { this.linked = linked; }

    public String getUserToken() { return userToken; }
    public void setUserToken(String userToken) { this.userToken = userToken; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Date getLastSeen() { return lastSeen; }
    public void setLastSeen(Date lastSeen) { this.lastSeen = lastSeen; }

    public Float getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Float batteryLevel) { this.batteryLevel = batteryLevel; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}