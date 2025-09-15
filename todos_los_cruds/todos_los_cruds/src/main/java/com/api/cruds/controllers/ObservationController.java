package com.api.cruds.controllers;

import com.api.cruds.models.ObservationModel;
import com.api.cruds.services.ObservationService;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/observaciones")
public class ObservationController {
    @Autowired
    private ObservationService observationService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> saveObservation(@RequestBody ObservationModel datos,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG CREAR OBSERVACION ===");
            System.out.println("Authorization header recibido: " + (authHeader != null ? "SI" : "NO"));

            // Validar datos de entrada
            if (datos.getTitulo() == null || datos.getTitulo().trim().isEmpty()) {
                System.out.println("ERROR: Título vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El título es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getNombre() == null || datos.getNombre().trim().isEmpty()) {
                System.out.println("ERROR: Nombre vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El nombre es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getDescripcion() == null || datos.getDescripcion().trim().isEmpty()) {
                System.out.println("ERROR: Descripción vacía");
                Map<String, String> error = new HashMap<>();
                error.put("error", "La descripción es obligatoria");
                return ResponseEntity.badRequest().body(error);
            }

            System.out.println("Datos validados correctamente:");
            System.out.println("- Título: " + datos.getTitulo());
            System.out.println("- Nombre: " + datos.getNombre());
            System.out.println("- Descripción: " + datos.getDescripcion().substring(0, Math.min(50, datos.getDescripcion().length())) + "...");

            // Extraer token
            String token = authHeader.substring(7);
            System.out.println("Token extraído (primeros 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");

            // Extraer email
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Email extraído del token: " + userEmail);

            if (userEmail == null || userEmail.trim().isEmpty()) {
                System.out.println("ERROR: Email extraído es nulo o vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "No se pudo extraer el email del token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            System.out.println("Llamando al servicio...");
            ObservationModel nuevaObservacion = this.observationService.saveObservation(datos, userEmail);

            System.out.println("Observación creada exitosamente con ID: " + nuevaObservacion.getId());
            System.out.println("=== FIN DEBUG ===");

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaObservacion);

        } catch (RuntimeException e) {
            System.err.println("RuntimeException en saveObservation: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            System.err.println("Exception en saveObservation: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getObservations(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG GET OBSERVACIONES ===");
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Email para GET: " + userEmail);

            ArrayList<ObservationModel> observations = this.observationService.getObservation(userEmail);
            System.out.println("Observaciones encontradas: " + observations.size());
            System.out.println("=== FIN DEBUG GET ===");

            return ResponseEntity.ok(observations);
        } catch (Exception e) {
            System.err.println("Error en getObservations: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener observaciones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getObservationById(@PathVariable("id") Integer id,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);

            Optional<ObservationModel> observation = this.observationService.getObservationById(id, userEmail);

            if (observation.isPresent()) {
                return ResponseEntity.ok(observation.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Observación no encontrada o sin permisos");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            System.err.println("Error en getObservationById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener observación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateObservationById(@RequestBody ObservationModel request,
                                                   @PathVariable("id") Integer id,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);

            ObservationModel updatedObservation = this.observationService.updateById(request, id, userEmail);
            return ResponseEntity.ok(updatedObservation);
        } catch (RuntimeException e) {
            System.err.println("RuntimeException en updateObservationById: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.err.println("Error en updateObservationById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar observación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteObservationById(@PathVariable("id") Integer id,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);

            boolean deleted = this.observationService.deleteObservation(id, userEmail);

            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("message", "Observación eliminada exitosamente");
                response.put("id", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Observación no encontrada o sin permisos para eliminar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            System.err.println("Error en deleteObservationById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar observación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}