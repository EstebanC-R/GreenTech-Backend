package com.api.cruds.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "datos_insumos")
public class InsumosModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumos")
    private Integer id;

    @Column(length = 50, nullable = false)
    private String producto;

    @Column(name = "cantidad_usada", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidadUsada;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_de_medida", nullable = false)
    private UnidadMedida medida = UnidadMedida.litros;

    @Column(name = "fecha_de_uso", nullable = false)
    private LocalDate fechaDeUso;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal costo;

    @Column(length = 50, nullable = false)
    private String proveedor;

    @Column(name = "user_email", length = 100, nullable = false)
    private String userEmail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // MÉTODOS LIFECYCLE PARA TIMESTAMPS AUTOMÁTICOS
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidadUsada() {
        return cantidadUsada;
    }

    public void setCantidadUsada(BigDecimal cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }

    public UnidadMedida getMedida() {
        return medida;
    }

    public void setMedida(UnidadMedida medida) {
        this.medida = medida;
    }

    public LocalDate getFechaDeUso() {
        return fechaDeUso;
    }

    public void setFechaDeUso(LocalDate fechaDeUso) {
        this.fechaDeUso = fechaDeUso;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}