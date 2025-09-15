package com.api.cruds.controllers;

import com.api.cruds.models.InsumosModel;
import com.api.cruds.services.InsumosService;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/insumos")
public class InsumosController {
    @Autowired
    private InsumosService insumosService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> saveInsumo(@RequestBody InsumosModel datos,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG CREAR INSUMO ===");
            System.out.println("Authorization header recibido: " + (authHeader != null ? "SI" : "NO"));

            // Validar datos de entrada
            if (datos.getProducto() == null || datos.getProducto().trim().isEmpty()) {
                System.out.println("ERROR: Producto vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El producto es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getCantidadUsada() == null || datos.getCantidadUsada().compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("ERROR: Cantidad usada inválida");
                Map<String, String> error = new HashMap<>();
                error.put("error", "La cantidad usada debe ser mayor a cero");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getMedida() == null) {
                System.out.println("ERROR: Unidad de medida vacía");
                Map<String, String> error = new HashMap<>();
                error.put("error", "La unidad de medida es obligatoria");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getFechaDeUso() == null) {
                System.out.println("ERROR: Fecha de uso vacía");
                Map<String, String> error = new HashMap<>();
                error.put("error", "La fecha de uso es obligatoria");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getCosto() == null || datos.getCosto().compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("ERROR: Costo inválido");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El costo debe ser mayor o igual a cero");
                return ResponseEntity.badRequest().body(error);
            }

            if (datos.getProveedor() == null || datos.getProveedor().trim().isEmpty()) {
                System.out.println("ERROR: Proveedor vacío");
                Map<String, String> error = new HashMap<>();
                error.put("error", "El proveedor es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            System.out.println("Datos validados correctamente:");
            System.out.println("- Producto: " + datos.getProducto());
            System.out.println("- Cantidad: " + datos.getCantidadUsada() + " " + datos.getMedida());
            System.out.println("- Fecha de uso: " + datos.getFechaDeUso());
            System.out.println("- Costo: $" + datos.getCosto());
            System.out.println("- Proveedor: " + datos.getProveedor());

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
            InsumosModel nuevoInsumo = this.insumosService.saveInsumo(datos, userEmail);

            System.out.println("Insumo creado exitosamente con ID: " + nuevoInsumo.getId());
            System.out.println("=== FIN DEBUG ===");

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInsumo);

        } catch (RuntimeException e) {
            System.err.println("RuntimeException en saveInsumo: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            System.err.println("Exception en saveInsumo: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getInsumos(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) String producto,
                                        @RequestParam(required = false) String proveedor,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        try {
            System.out.println("=== DEBUG GET INSUMOS ===");
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Email para GET: " + userEmail);
            System.out.println("Filtros recibidos:");
            System.out.println("- Producto: " + (producto != null ? producto : "ninguno"));
            System.out.println("- Proveedor: " + (proveedor != null ? proveedor : "ninguno"));
            System.out.println("- Fecha desde: " + (fechaDesde != null ? fechaDesde : "ninguna"));
            System.out.println("- Fecha hasta: " + (fechaHasta != null ? fechaHasta : "ninguna"));

            ArrayList<InsumosModel> insumos;

            // Si hay filtros, usar búsqueda con filtros
            if (producto != null || proveedor != null || fechaDesde != null || fechaHasta != null) {
                insumos = this.insumosService.getInsumosWithFilters(userEmail, producto, proveedor, fechaDesde, fechaHasta);
            } else {
                // Sin filtros, obtener todos
                insumos = this.insumosService.getInsumos(userEmail);
            }

            System.out.println("Insumos encontrados: " + insumos.size());
            System.out.println("=== FIN DEBUG GET ===");

            return ResponseEntity.ok(insumos);
        } catch (Exception e) {
            System.err.println("Error en getInsumos: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener insumos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getInsumoById(@PathVariable("id") Integer id,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG GET INSUMO BY ID ===");
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Buscando insumo ID: " + id + " para usuario: " + userEmail);

            Optional<InsumosModel> insumo = this.insumosService.getInsumoById(id, userEmail);

            if (insumo.isPresent()) {
                System.out.println("Insumo encontrado");
                return ResponseEntity.ok(insumo.get());
            } else {
                System.out.println("Insumo no encontrado o sin permisos");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Insumo no encontrado o sin permisos de acceso");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            System.err.println("Error en getInsumoById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener insumo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateInsumoById(@RequestBody InsumosModel request,
                                              @PathVariable("id") Integer id,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG UPDATE INSUMO ===");

            // Validar datos de entrada
            if (request.getProducto() == null || request.getProducto().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El producto es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getCantidadUsada() == null || request.getCantidadUsada().compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La cantidad usada debe ser mayor a cero");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getMedida() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La unidad de medida es obligatoria");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getFechaDeUso() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "La fecha de uso es obligatoria");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getCosto() == null || request.getCosto().compareTo(BigDecimal.ZERO) < 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El costo debe ser mayor o igual a cero");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getProveedor() == null || request.getProveedor().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El proveedor es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Actualizando insumo ID: " + id + " para usuario: " + userEmail);

            InsumosModel updatedInsumo = this.insumosService.updateById(request, id, userEmail);
            System.out.println("Insumo actualizado exitosamente");
            return ResponseEntity.ok(updatedInsumo);

        } catch (RuntimeException e) {
            System.err.println("RuntimeException en updateInsumoById: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            if (e.getMessage().contains("No tiene permisos")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.err.println("Error en updateInsumoById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar insumo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteInsumoById(@PathVariable("id") Integer id,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== DEBUG DELETE INSUMO ===");
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);
            System.out.println("Eliminando insumo ID: " + id + " para usuario: " + userEmail);

            boolean deleted = this.insumosService.deleteInsumo(id, userEmail);

            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                System.out.println("Insumo eliminado exitosamente");
                response.put("message", "Insumo eliminado exitosamente");
                response.put("id", id);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("No se pudo eliminar el insumo");
                response.put("error", "Insumo no encontrado o sin permisos para eliminar. Solo el administrador propietario puede eliminar.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            System.err.println("Error en deleteInsumoById: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar insumo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Endpoint adicional para obtener estadísticas (opcional)
    @GetMapping("/stats")
    public ResponseEntity<?> getInsumosStats(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractEmail(token);

            // Aquí podrías agregar lógica para estadísticas agregadas
            // Por ahora, solo devuelve los insumos con filtros
            ArrayList<InsumosModel> insumos = this.insumosService.getInsumosWithFilters(
                    userEmail, null, null, fechaDesde, fechaHasta
            );

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalInsumos", insumos.size());

            if (!insumos.isEmpty()) {
                BigDecimal totalCosto = insumos.stream()
                        .map(InsumosModel::getCosto)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("costoTotal", totalCosto);
                stats.put("costoPromedio", totalCosto.divide(new BigDecimal(insumos.size()), 2, BigDecimal.ROUND_HALF_UP));
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}