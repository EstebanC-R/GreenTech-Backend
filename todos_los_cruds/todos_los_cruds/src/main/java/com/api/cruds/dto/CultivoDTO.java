package com.api.cruds.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CultivoDTO {

    private Long idCultivo;
    private Long deviceIdFk;
    private String usuarioResponsable;
    private String nombreCultivo;
    private String tipoDeCultivo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPlantacion;

    private BigDecimal produccionEstimada;
    private BigDecimal humedadSueloMin;
    private BigDecimal humedadSueloMax;
    private BigDecimal temperaturaMin;
    private BigDecimal temperaturaMax;
    private String estadoCultivo;
    private String descripcion;

    // Info adicional del device (si está asociado)
    private String deviceCode;
    private String deviceName;

    // Constructores
    public CultivoDTO() {}

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

    public String getEstadoCultivo() { return estadoCultivo; }
    public void setEstadoCultivo(String estadoCultivo) { this.estadoCultivo = estadoCultivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
}
