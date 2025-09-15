package com.api.cruds.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CULTIVOS")
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CULTIVO")
    private Long idCultivo;

    @Column(name = "DEVICE_ID_FK")
    private Long deviceIdFk;

    @Column(name = "USUARIO_RESPONSABLE", nullable = false, length = 100)
    private String usuarioResponsable;

    @Column(name = "NOMBRE_CULTIVO", nullable = false, length = 100)
    private String nombreCultivo;

    @Column(name = "TIPO_DE_CULTIVO", nullable = false, length = 50)
    private String tipoDeCultivo;

    @Column(name = "FECHA_REGISTRO")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    @Column(name = "FECHA_PLANTACION")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPlantacion;

    @Column(name = "PRODUCCION_ESTIMADA", precision = 8, scale = 2)
    private BigDecimal produccionEstimada;

    @Column(name = "HUMEDAD_SUELO_MIN", precision = 5, scale = 2)
    private BigDecimal humedadSueloMin;

    @Column(name = "HUMEDAD_SUELO_MAX", precision = 5, scale = 2)
    private BigDecimal humedadSueloMax;

    @Column(name = "TEMPERATURA_MIN", precision = 5, scale = 2)
    private BigDecimal temperaturaMin;

    @Column(name = "TEMPERATURA_MAX", precision = 5, scale = 2)
    private BigDecimal temperaturaMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_CULTIVO")
    private EstadoCultivo estadoCultivo = EstadoCultivo.EN_PROGRESO;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Relación con Device (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID_FK", insertable = false, updatable = false)
    private Device device;

    // Relación con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_RESPONSABLE", insertable = false, updatable = false)
    private UserModel usuario;

    // Enum para estado
    public enum EstadoCultivo {
        EN_PROGRESO, COSECHADO, TERMINADO
    }

    // Constructor por defecto
    public Cultivo() {
        this.fechaRegistro = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdCultivo() { return idCultivo; }
    public void setIdCultivo(Long idCultivo) { this.idCultivo = idCultivo; }

    public Long getDeviceIdFk() { return deviceIdFk; }
    public void setDeviceIdFk(Long deviceIdFk) { this.deviceIdFk = deviceIdFk; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }

    public String getNombreCultivo() { return nombreCultivo; }
    public void setNombreCultivo(String nombreCultivo) { this.nombreCultivo = nombreCultivo; }

    public String getTipoDeCultivo() { return tipoDeCultivo; }
    public void setTipoDeCultivo(String tipoDeCultivo) { this.tipoDeCultivo = tipoDeCultivo; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDate getFechaPlantacion() { return fechaPlantacion; }
    public void setFechaPlantacion(LocalDate fechaPlantacion) { this.fechaPlantacion = fechaPlantacion; }

    public BigDecimal getProduccionEstimada() { return produccionEstimada; }
    public void setProduccionEstimada(BigDecimal produccionEstimada) { this.produccionEstimada = produccionEstimada; }

    public BigDecimal getHumedadSueloMin() { return humedadSueloMin; }
    public void setHumedadSueloMin(BigDecimal humedadSueloMin) { this.humedadSueloMin = humedadSueloMin; }

    public BigDecimal getHumedadSueloMax() { return humedadSueloMax; }
    public void setHumedadSueloMax(BigDecimal humedadSueloMax) { this.humedadSueloMax = humedadSueloMax; }

    public BigDecimal getTemperaturaMin() { return temperaturaMin; }
    public void setTemperaturaMin(BigDecimal temperaturaMin) { this.temperaturaMin = temperaturaMin; }

    public BigDecimal getTemperaturaMax() { return temperaturaMax; }
    public void setTemperaturaMax(BigDecimal temperaturaMax) { this.temperaturaMax = temperaturaMax; }

    public EstadoCultivo getEstadoCultivo() { return estadoCultivo; }
    public void setEstadoCultivo(EstadoCultivo estadoCultivo) { this.estadoCultivo = estadoCultivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public UserModel getUsuario() { return usuario; }
    public void setUsuario(UserModel usuario) { this.usuario = usuario; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

