package com.api.cruds.dto;

import java.util.Date;

public class UserProfileResponse {
    private String message;
    private String email;
    private String rol;
    private Date fechaIngreso;
    private String estado;
    private String adminAsignado;
    private String adminEfectivo;

    public UserProfileResponse(String message, String email, String rol, Date fechaIngreso, String estado, String adminAsignado, String adminEfectivo) {
        this.message = message;
        this.email = email;
        this.rol = rol;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.adminAsignado = adminAsignado;
        this.adminEfectivo = adminEfectivo;
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAdminAsignado() {
        return adminAsignado;
    }

    public void setAdminAsignado(String adminAsignado) {
        this.adminAsignado = adminAsignado;
    }

    public String getAdminEfectivo() {
        return adminEfectivo;
    }

    public void setAdminEfectivo(String adminEfectivo) {
        this.adminEfectivo = adminEfectivo;
    }
}
