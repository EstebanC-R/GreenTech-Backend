package com.api.cruds.services;

import com.api.cruds.dto.CultivoDTO;
import com.api.cruds.models.Cultivo;
import com.api.cruds.models.Device;
import com.api.cruds.repositories.CultivoRepository;
import com.api.cruds.repositories.DeviceRepository;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CultivoService {

    @Autowired
    private CultivoRepository cultivoRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // Obtener admin efectivo (igual que en el controller de sensores)
    private String getAdminEfectivo(String userEmail) {
        try {
            String sql = """
                SELECT admin_efectivo 
                FROM vista_usuarios_con_admin 
                WHERE usuario_email = ? AND admin_efectivo IS NOT NULL
                """;

            List<String> result = jdbcTemplate.queryForList(sql, String.class, userEmail);
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    // Verificar si es administrador usando el token
    private boolean esAdministrador(String token) {
        try {
            String rol = jwtUtil.extractRol(token);
            return "ADMINISTRADOR".equals(rol);
        } catch (Exception e) {
            return false;
        }
    }

    // Obtener todos los cultivos del usuario (considerando admin-empleado)
    public List<CultivoDTO> getCultivosByUsuario(String userEmail) {
        String adminEfectivo = getAdminEfectivo(userEmail);
        if (adminEfectivo == null) {
            return List.of(); // Sin acceso
        }

        List<Cultivo> cultivos = cultivoRepository.findCultivosConDeviceByUsuario(adminEfectivo);
        return cultivos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Crear nuevo cultivo - SOLO ADMINISTRADORES
    public CultivoDTO createCultivo(CultivoDTO cultivoDTO, String userEmail, String token) {
        // Verificar que sea administrador
        if (!esAdministrador(token)) {
            throw new RuntimeException("Solo los administradores pueden crear cultivos");
        }

        String adminEfectivo = getAdminEfectivo(userEmail);
        if (adminEfectivo == null) {
            throw new RuntimeException("Usuario sin permisos para crear cultivos");
        }

        Cultivo cultivo = new Cultivo();
        mapDTOToEntity(cultivoDTO, cultivo);
        cultivo.setUsuarioResponsable(adminEfectivo); // Asignar al admin efectivo

        cultivo = cultivoRepository.save(cultivo);
        return convertToDTO(cultivo);
    }

    // Actualizar cultivo - SOLO ADMINISTRADORES
    public CultivoDTO updateCultivo(Long id, CultivoDTO cultivoDTO, String userEmail, String token) {
        // Verificar que sea administrador
        if (!esAdministrador(token)) {
            throw new RuntimeException("Solo los administradores pueden actualizar cultivos");
        }

        String adminEfectivo = getAdminEfectivo(userEmail);
        if (adminEfectivo == null) {
            throw new RuntimeException("Usuario sin permisos");
        }

        Optional<Cultivo> cultivoOpt = cultivoRepository.findById(id);
        if (cultivoOpt.isEmpty() || !cultivoOpt.get().getUsuarioResponsable().equals(adminEfectivo)) {
            throw new RuntimeException("Cultivo no encontrado o sin permisos");
        }

        Cultivo cultivo = cultivoOpt.get();
        mapDTOToEntity(cultivoDTO, cultivo);
        cultivo.setUpdatedAt(LocalDateTime.now());

        cultivo = cultivoRepository.save(cultivo);
        return convertToDTO(cultivo);
    }

    // Eliminar cultivo - SOLO ADMINISTRADORES
    public void deleteCultivo(Long id, String userEmail, String token) {
        // Verificar que sea administrador
        if (!esAdministrador(token)) {
            throw new RuntimeException("Solo los administradores pueden eliminar cultivos");
        }

        String adminEfectivo = getAdminEfectivo(userEmail);
        if (adminEfectivo == null) {
            throw new RuntimeException("Usuario sin permisos");
        }

        Optional<Cultivo> cultivoOpt = cultivoRepository.findById(id);
        if (cultivoOpt.isEmpty() || !cultivoOpt.get().getUsuarioResponsable().equals(adminEfectivo)) {
            throw new RuntimeException("Cultivo no encontrado o sin permisos");
        }

        cultivoRepository.deleteById(id);
    }

    // Convertir Entity a DTO
    private CultivoDTO convertToDTO(Cultivo cultivo) {
        CultivoDTO dto = new CultivoDTO();
        dto.setIdCultivo(cultivo.getIdCultivo());
        dto.setDeviceIdFk(cultivo.getDeviceIdFk());
        dto.setUsuarioResponsable(cultivo.getUsuarioResponsable());
        dto.setNombreCultivo(cultivo.getNombreCultivo());
        dto.setTipoDeCultivo(cultivo.getTipoDeCultivo());
        dto.setFechaRegistro(cultivo.getFechaRegistro());
        dto.setFechaPlantacion(cultivo.getFechaPlantacion());
        dto.setProduccionEstimada(cultivo.getProduccionEstimada());
        dto.setHumedadSueloMin(cultivo.getHumedadSueloMin());
        dto.setHumedadSueloMax(cultivo.getHumedadSueloMax());
        dto.setTemperaturaMin(cultivo.getTemperaturaMin());
        dto.setTemperaturaMax(cultivo.getTemperaturaMax());
        dto.setEstadoCultivo(cultivo.getEstadoCultivo().toString());
        dto.setDescripcion(cultivo.getDescripcion());

        // Info del device si está asociado
        if (cultivo.getDevice() != null) {
            dto.setDeviceCode(cultivo.getDevice().getDeviceCode());
            dto.setDeviceName(cultivo.getDevice().getDeviceName());
        }

        return dto;
    }

    // Mapear DTO a Entity
    private void mapDTOToEntity(CultivoDTO dto, Cultivo entity) {
        entity.setDeviceIdFk(dto.getDeviceIdFk());
        entity.setNombreCultivo(dto.getNombreCultivo());
        entity.setTipoDeCultivo(dto.getTipoDeCultivo());
        entity.setFechaPlantacion(dto.getFechaPlantacion());
        entity.setProduccionEstimada(dto.getProduccionEstimada());
        entity.setHumedadSueloMin(dto.getHumedadSueloMin());
        entity.setHumedadSueloMax(dto.getHumedadSueloMax());
        entity.setTemperaturaMin(dto.getTemperaturaMin());
        entity.setTemperaturaMax(dto.getTemperaturaMax());
        entity.setDescripcion(dto.getDescripcion());

        if (dto.getEstadoCultivo() != null) {
            entity.setEstadoCultivo(Cultivo.EstadoCultivo.valueOf(dto.getEstadoCultivo()));
        }
    }
}