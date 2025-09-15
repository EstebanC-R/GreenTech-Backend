package com.api.cruds.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        System.out.println("=== ENTRANDO AL HANDLER DE DataIntegrityViolationException ===");
        Map<String, Object> response = new HashMap<>();

        // Obtener el mensaje más específico
        String mensaje = ex.getMostSpecificCause().getMessage().toLowerCase();

        // Log para debugging
        System.out.println("Error de integridad completo: " + ex.getMostSpecificCause().getMessage());
        System.out.println("Error de integridad procesado: " + mensaje);
        System.out.println("Tipo de excepción: " + ex.getClass().getName());

        // Mejorar la detección de violaciones de integridad
        if (mensaje.contains("uq_empleado_cedula") ||
                mensaje.contains("datos_empleados.cedula") ||
                mensaje.contains("cedula") && (mensaje.contains("duplicate") || mensaje.contains("unique"))) {

            response.put("message", "Esta cédula ya está registrada en el sistema");
            response.put("error", "cedula_duplicada");
            response.put("field", "cedula");
            response.put("status", HttpStatus.CONFLICT.value());

        } else if (mensaje.contains("uq_empleado_celular") ||
                mensaje.contains("datos_empleados.celular") ||
                mensaje.contains("celular") && (mensaje.contains("duplicate") || mensaje.contains("unique"))) {

            response.put("message", "Este número de celular ya está registrado en el sistema");
            response.put("error", "celular_duplicado");
            response.put("field", "celular");
            response.put("status", HttpStatus.CONFLICT.value());

        } else if (mensaje.contains("uq_empleado_correo") ||
                mensaje.contains("datos_empleados.correo") ||
                mensaje.contains("correo") && (mensaje.contains("duplicate") || mensaje.contains("unique")) ||
                mensaje.contains("email") && (mensaje.contains("duplicate") || mensaje.contains("unique"))) {

            response.put("message", "Este correo electrónico ya está registrado en el sistema");
            response.put("error", "correo_duplicado");
            response.put("field", "correo");
            response.put("status", HttpStatus.CONFLICT.value());

        } else {
            // Mensaje genérico para otros casos de integridad
            response.put("message", "Los datos ingresados ya existen en el sistema. Verifique cédula, celular y correo electrónico.");
            response.put("error", "datos_duplicados");
            response.put("field", "multiple");
            response.put("status", HttpStatus.CONFLICT.value());
        }

        // Agregar información adicional para debugging
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", "/api/empleados");

        System.out.println("Respuesta JSON que se enviará: " + response);
        System.out.println("Status HTTP: " + HttpStatus.CONFLICT.value());
        System.out.println("=== FIN HANDLER DataIntegrityViolationException ===");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println("=== ENTRANDO AL HANDLER DE VALIDACIÓN ===");
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        response.put("message", "Error de validación en los datos enviados");
        response.put("error", "validation_error");
        response.put("fields", fieldErrors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", System.currentTimeMillis());

        System.out.println("Errores de validación que se enviarán: " + response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFoundException(FileNotFoundException ex) {
        System.out.println("=== ENTRANDO AL HANDLER DE ARCHIVO NO ENCONTRADO ===");
        Map<String, Object> response = new HashMap<>();

        response.put("error", "archivo_no_encontrado");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "No hay archivos subidos para este empleado");
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", System.currentTimeMillis());

        System.out.println("Respuesta de archivo no encontrado: " + response);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        System.out.println("=== ENTRANDO AL HANDLER GENÉRICO ===");
        System.out.println("Tipo de excepción: " + ex.getClass().getName());
        System.out.println("Mensaje de la excepción: " + ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "error_interno");
        response.put("message", "Error interno del servidor. Por favor, contacte al administrador.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("timestamp", System.currentTimeMillis());

        // Log del error real para debugging (sin exponer detalles sensibles)
        System.err.println("Error no manejado: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        ex.printStackTrace();

        System.out.println("Respuesta genérica que se enviará: " + response);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handler específico para errores de empleado no encontrado
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println("=== ENTRANDO AL HANDLER DE IllegalArgumentException ===");
        Map<String, Object> response = new HashMap<>();

        response.put("error", "argumento_invalido");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Argumento inválido proporcionado");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", System.currentTimeMillis());

        System.out.println("Respuesta de argumento inválido: " + response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}