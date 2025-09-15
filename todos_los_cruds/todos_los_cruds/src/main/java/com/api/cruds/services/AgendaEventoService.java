package com.api.cruds.services;

import com.api.cruds.dto.AgendaEventoDTO;
import com.api.cruds.dto.EstadisticasAgendaDTO;
import com.api.cruds.models.AgendaEvento;
import com.api.cruds.repositories.AgendaEventoRepository;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgendaEventoService {

    @Autowired
    private AgendaEventoRepository agendaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<AgendaEventoDTO> getEventosConPermisos(String token) {
        String userEmail = jwtUtil.extractEmail(token);
        List<AgendaEvento> eventos = agendaRepository.findEventosConPermisos(userEmail);

        return eventos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AgendaEventoDTO getEventoConPermisos(Long idEvento, String token) {
        String userEmail = jwtUtil.extractEmail(token);
        AgendaEvento evento = agendaRepository.findEventoConPermisos(idEvento, userEmail)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado o sin permisos"));

        return convertToDTO(evento);
    }

    public AgendaEventoDTO crearEvento(AgendaEventoDTO createDTO, String token) {
        String userEmail = jwtUtil.extractEmail(token);
        String rol = jwtUtil.extractRol(token);

        // Solo administradores pueden crear eventos
        if (!"ADMINISTRADOR".equals(rol)) {
            throw new RuntimeException("Solo los administradores pueden crear eventos");
        }

        // Obtener admin efectivo (para administradores es su propio email)
        String adminEfectivo = agendaRepository.getAdminEfectivo(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario sin permisos para crear eventos"));

        AgendaEvento evento = new AgendaEvento();
        evento.setUserEmail(adminEfectivo);
        evento.setTitulo(createDTO.getTitulo());
        evento.setDescripcion(createDTO.getDescripcion());
        evento.setFechaEvento(createDTO.getFechaEvento());
        evento.setTipo(AgendaEvento.TipoEvento.valueOf(createDTO.getTipo().toUpperCase()));
        evento.setCompletado(false);

        AgendaEvento savedEvento = agendaRepository.save(evento);
        return convertToDTO(savedEvento);
    }

    public AgendaEventoDTO actualizarEvento(Long idEvento, AgendaEventoDTO updateDTO, String token) {
        String userEmail = jwtUtil.extractEmail(token);
        String rol = jwtUtil.extractRol(token);

        // Solo administradores pueden editar
        if (!"ADMINISTRADOR".equals(rol)) {
            throw new RuntimeException("Solo los administradores pueden editar eventos");
        }

        // Verificar que el evento existe y le pertenece al usuario
        AgendaEvento evento = agendaRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Verificar que es el propietario del evento
        if (!userEmail.equals(evento.getUserEmail())) {
            throw new RuntimeException("No tiene permisos para editar este evento");
        }

        // Actualizar campos si están presentes
        if (updateDTO.getTitulo() != null) {
            evento.setTitulo(updateDTO.getTitulo());
        }
        if (updateDTO.getDescripcion() != null) {
            evento.setDescripcion(updateDTO.getDescripcion());
        }
        if (updateDTO.getFechaEvento() != null) {
            evento.setFechaEvento(updateDTO.getFechaEvento());
        }
        if (updateDTO.getTipo() != null) {
            evento.setTipo(AgendaEvento.TipoEvento.valueOf(updateDTO.getTipo().toUpperCase()));
        }
        if (updateDTO.getCompletado() != null) {
            evento.setCompletado(updateDTO.getCompletado());
        }

        AgendaEvento updatedEvento = agendaRepository.save(evento);
        return convertToDTO(updatedEvento);
    }

    public AgendaEventoDTO completarEvento(Long idEvento, String token) {
        AgendaEventoDTO updateDTO = new AgendaEventoDTO();
        updateDTO.setCompletado(true);
        return actualizarEvento(idEvento, updateDTO, token);
    }

    public void eliminarEvento(Long idEvento, String token) {
        String userEmail = jwtUtil.extractEmail(token);
        String rol = jwtUtil.extractRol(token);

        // Solo administradores pueden eliminar
        if (!"ADMINISTRADOR".equals(rol)) {
            throw new RuntimeException("Solo los administradores pueden eliminar eventos");
        }

        // Verificar que el evento existe y le pertenece al usuario
        AgendaEvento evento = agendaRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Verificar que es el propietario del evento
        if (!userEmail.equals(evento.getUserEmail())) {
            throw new RuntimeException("No tiene permisos para eliminar este evento");
        }

        agendaRepository.deleteById(idEvento);
    }

    public EstadisticasAgendaDTO getEstadisticas(String token) {
        String userEmail = jwtUtil.extractEmail(token);

        System.out.println("🔧 DEBUG - Email usuario: " + userEmail);

        // CAMBIO PRINCIPAL: Usar List<Object[]> en lugar de Object[]
        List<Object[]> resultList = agendaRepository.getEstadisticasConPermisos(userEmail);

        System.out.println("🔧 DEBUG - Resultado query (List): " + resultList);
        System.out.println("🔧 DEBUG - List size: " + (resultList != null ? resultList.size() : "null"));

        if (resultList != null && !resultList.isEmpty()) {
            Object[] stats = resultList.get(0); // Obtener la primera (y única) fila

            System.out.println("🔧 DEBUG - Stats array: " + Arrays.toString(stats));

            if (stats != null && stats.length >= 4) {
                try {
                    Long totalEventos = convertToLong(stats[0]);
                    Long eventosPendientes = convertToLong(stats[1]);
                    Long eventosCompletados = convertToLong(stats[2]);
                    Long eventosHoy = convertToLong(stats[3]);

                    System.out.println("🔧 DEBUG - Estadísticas convertidas:");
                    System.out.println("   totalEventos: " + totalEventos);
                    System.out.println("   eventosPendientes: " + eventosPendientes);
                    System.out.println("   eventosCompletados: " + eventosCompletados);
                    System.out.println("   eventosHoy: " + eventosHoy);

                    return new EstadisticasAgendaDTO(totalEventos, eventosPendientes, eventosCompletados, eventosHoy);
                } catch (Exception e) {
                    System.err.println("Error convirtiendo estadísticas: " + e.getMessage());
                    e.printStackTrace();
                    return new EstadisticasAgendaDTO(0L, 0L, 0L, 0L);
                }
            }
        }

        System.out.println("🔧 DEBUG - No se encontraron datos");
        return new EstadisticasAgendaDTO(0L, 0L, 0L, 0L);
    }

    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof BigInteger) return ((BigInteger) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    public List<AgendaEventoDTO> getEventosPendientes(String token) {
        String userEmail = jwtUtil.extractEmail(token);
        List<AgendaEvento> eventos = agendaRepository.findEventosPendientesConPermisos(userEmail);

        return eventos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AgendaEventoDTO> getEventosHoy(String token) {
        String userEmail = jwtUtil.extractEmail(token);
        List<AgendaEvento> eventos = agendaRepository.findEventosHoyConPermisos(userEmail);

        return eventos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // MÉTODO CONVERTIDOR CORREGIDO - Este era el problema principal
    private AgendaEventoDTO convertToDTO(AgendaEvento evento) {
        return new AgendaEventoDTO(
                evento.getIdEvento(),
                evento.getUserEmail(),  // Se mapea a 'propietario' en el DTO
                evento.getCreatedAt(),
                evento.getUpdatedAt(),
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaEvento(),
                evento.getTipo().toString(),
                evento.getCompletado()
        );
    }
}