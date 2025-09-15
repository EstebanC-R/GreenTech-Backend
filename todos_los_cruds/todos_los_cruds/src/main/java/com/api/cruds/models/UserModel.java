package com.api.cruds.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USUARIOS")
public class UserModel {

    @Id
    @Column(name = "id_usuario")
    private String email;

    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;

    @Column(name = "ID_ROL")
    private Integer idRol;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "rol")
    private String rol = "EMPLEADO";

    // Constructores
    public UserModel() {}

    public UserModel(String email, String passwordHash, String rol) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Getters y Setters - ELIMINAR getId() y setId()
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}