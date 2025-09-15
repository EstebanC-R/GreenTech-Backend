package com.api.cruds.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String rol;
    private String message;

    // Constructores
    public LoginResponse() {}

    // Constructor original (mantener compatibilidad)
    public LoginResponse(String token, String email, String rol) {
        this.token = token;
        this.email = email;
        this.rol = rol;
    }

    // Constructor original para solo mensaje (mantener compatibilidad)
    public LoginResponse(String message) {
        this.message = message;
    }

    // NUEVO: Constructor completo (necesario para el AuthService)
    public LoginResponse(String message, String token, String rol, String email) {
        this.message = message;
        this.token = token;
        this.rol = rol;
        this.email = email;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
