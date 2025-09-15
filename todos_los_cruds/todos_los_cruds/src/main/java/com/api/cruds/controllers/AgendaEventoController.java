package com.api.cruds.controllers;


import com.api.cruds.dto.AgendaEventoDTO;
import com.api.cruds.dto.EstadisticasAgendaDTO;
import com.api.cruds.services.AgendaEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda")
public class AgendaEventoController {

    @Autowired
    private AgendaEventoService agendaService;

    /**
     * Obtener todos los eventos (con permisos)
     * GET /api/agenda/eventos
     */
    @GetMapping("/eventos")
    public ResponseEntity<List<AgendaEventoDTO>> getEventos(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<AgendaEventoDTO> eventos = agendaService.getEventosConPermisos(token);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener evento específico
     * GET /api/agenda/eventos/{id}
     */
    @GetMapping("/eventos/{id}")
    public ResponseEntity<AgendaEventoDTO> getEvento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            AgendaEventoDTO evento = agendaService.getEventoConPermisos(id, token);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crear nuevo evento (solo ADMINISTRADORES)
     * POST /api/agenda/eventos
     */
    @PostMapping("/eventos")
    public ResponseEntity<?> crearEvento(
            @Validated(AgendaEventoDTO.Create.class) @RequestBody AgendaEventoDTO createDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            AgendaEventoDTO evento = agendaService.crearEvento(createDTO, token);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear el evento"));
        }
    }

    /**
     * Actualizar evento (solo ADMINISTRADORES propietarios)
     * PUT /api/agenda/eventos/{id}
     */
    @PutMapping("/eventos/{id}")
    public ResponseEntity<?> actualizarEvento(
            @PathVariable Long id,
            @RequestBody AgendaEventoDTO updateDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            AgendaEventoDTO evento = agendaService.actualizarEvento(id, updateDTO, token);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar el evento"));
        }
    }

    /**
     * Marcar evento como completado
     * PATCH /api/agenda/eventos/{id}/completar
     */
    @PatchMapping("/eventos/{id}/completar")
    public ResponseEntity<?> completarEvento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            AgendaEventoDTO evento = agendaService.completarEvento(id, token);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al completar el evento"));
        }
    }

    /**
     * Eliminar evento (solo ADMINISTRADORES propietarios)
     * DELETE /api/agenda/eventos/{id}
     */
    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<?> eliminarEvento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            agendaService.eliminarEvento(id, token);
            return ResponseEntity.ok(Map.of("message", "Evento eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al eliminar el evento"));
        }
    }

    /**
     * Obtener estadísticas de la agenda
     * GET /api/agenda/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasAgendaDTO> getEstadisticas(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            EstadisticasAgendaDTO stats = agendaService.getEstadisticas(token);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener eventos pendientes
     * GET /api/agenda/eventos/pendientes
     */
    @GetMapping("/eventos/pendientes")
    public ResponseEntity<List<AgendaEventoDTO>> getEventosPendientes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<AgendaEventoDTO> eventos = agendaService.getEventosPendientes(token);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener eventos de hoy
     * GET /api/agenda/eventos/hoy
     */
    @GetMapping("/eventos/hoy")
    public ResponseEntity<List<AgendaEventoDTO>> getEventosHoy(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            List<AgendaEventoDTO> eventos = agendaService.getEventosHoy(token);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}