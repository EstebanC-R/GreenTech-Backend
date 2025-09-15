package com.api.cruds.controllers;

import com.api.cruds.dto.CultivoDTO;
import com.api.cruds.services.CultivoService;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cultivos")
public class CultivoController {

    @Autowired
    private CultivoService cultivoService;

    @Autowired
    private JwtUtil jwtUtil;

    // Obtener todos los cultivos del usuario (sin restricción de rol)
    @GetMapping
    public ResponseEntity<List<CultivoDTO>> getCultivos(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);

            List<CultivoDTO> cultivos = cultivoService.getCultivosByUsuario(userEmail);
            return ResponseEntity.ok(cultivos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Crear nuevo cultivo - SOLO ADMINISTRADORES
    @PostMapping
    public ResponseEntity<?> createCultivo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CultivoDTO cultivoDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);

            // Pasar el token completo al service para verificar rol
            CultivoDTO nuevoCultivo = cultivoService.createCultivo(cultivoDTO, userEmail, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCultivo);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    // Actualizar cultivo - SOLO ADMINISTRADORES
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCultivo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody CultivoDTO cultivoDTO) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);

            // Pasar el token completo al service para verificar rol
            CultivoDTO cultivoActualizado = cultivoService.updateCultivo(id, cultivoDTO, userEmail, token);
            return ResponseEntity.ok(cultivoActualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    // Eliminar cultivo - SOLO ADMINISTRADORES
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCultivo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);

            // Pasar el token completo al service para verificar rol
            cultivoService.deleteCultivo(id, userEmail, token);
            return ResponseEntity.ok(Map.of("message", "Cultivo eliminado correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}