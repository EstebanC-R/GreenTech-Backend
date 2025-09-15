package com.api.cruds.controllers;

import com.api.cruds.models.EmpleadoModel;
import com.api.cruds.models.EstadoEmpleado;
import com.api.cruds.repositories.IEmpleadosRepository;
import com.api.cruds.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private IEmpleadosRepository empleadosRepository;

    @GetMapping
    public ArrayList<EmpleadoModel> getEmpleados() {
        ArrayList<EmpleadoModel> empleados = this.empleadoService.getEmpleado();

        for(EmpleadoModel emple : empleados){
            System.out.println("ID: " + emple.getId() + ", fecha de ingreso: " + emple.getFechaDeIngreso());
        }

        return empleados;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EmpleadoModel>> buscarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) EstadoEmpleado estado) {

        List<EmpleadoModel> empleados = empleadoService.buscarConFiltros(nombre, cedula, estado);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/verificar-duplicados")
    public ResponseEntity<Map<String, Boolean>> verificarDuplicados(
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String celular,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) Long excluir) {

        System.out.println("=== VERIFICANDO DUPLICADOS ===");
        System.out.println("Cédula: " + cedula);
        System.out.println("Celular: " + celular);
        System.out.println("Correo: " + correo);
        System.out.println("Excluir ID: " + excluir);

        Map<String, Boolean> resultado = new HashMap<>();

        try {
            // Verificar cédula
            if (cedula != null && !cedula.trim().isEmpty()) {
                boolean cedulaExiste;
                if (excluir != null) {
                    cedulaExiste = empleadosRepository.existsByCedulaAndIdNot(cedula.trim(), excluir);
                } else {
                    cedulaExiste = empleadosRepository.existsByCedula(cedula.trim());
                }
                resultado.put("cedulaExiste", cedulaExiste);
            } else {
                resultado.put("cedulaExiste", false);
            }

            // Verificar celular
            if (celular != null && !celular.trim().isEmpty()) {
                boolean celularExiste;
                if (excluir != null) {
                    celularExiste = empleadosRepository.existsByCelularAndIdNot(celular.trim(), excluir);
                } else {
                    celularExiste = empleadosRepository.existsByCelular(celular.trim());
                }
                resultado.put("celularExiste", celularExiste);
            } else {
                resultado.put("celularExiste", false);
            }

            // Verificar correo
            if (correo != null && !correo.trim().isEmpty()) {
                boolean correoExiste;
                if (excluir != null) {
                    correoExiste = empleadosRepository.existsByCorreoAndIdNot(correo.trim(), excluir);
                } else {
                    correoExiste = empleadosRepository.existsByCorreo(correo.trim());
                }
                resultado.put("correoExiste", correoExiste);
            } else {
                resultado.put("correoExiste", false);
            }

            System.out.println("Resultado verificación: " + resultado);
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            System.err.println("Error al verificar duplicados: " + e.getMessage());
            e.printStackTrace();

            // En caso de error, devolver falsos para no bloquear
            resultado.put("cedulaExiste", false);
            resultado.put("celularExiste", false);
            resultado.put("correoExiste", false);

            return ResponseEntity.ok(resultado);
        }
    }

    @PostMapping
    public ResponseEntity<EmpleadoModel> saveEmpleado(@RequestBody EmpleadoModel empleado) {
        try {
            System.out.println("=== CREANDO EMPLEADO ===");
            System.out.println("Datos recibidos: " + empleado);

            EmpleadoModel savedEmpleado = empleadoService.saveEmpleado(empleado);

            System.out.println("Empleado guardado exitosamente: " + savedEmpleado.getId());
            return ResponseEntity.ok(savedEmpleado);
        } catch (Exception e) {
            System.err.println("Error al guardar empleado: " + e.getMessage());
            e.printStackTrace();
            // No usar badRequest().build() porque no devuelve información del error
            // El GlobalExceptionHandler se encargará de manejar la excepción
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoModel> getEmpleadoById(@PathVariable Long id) {
        Optional<EmpleadoModel> empleado = empleadoService.getEmpleadoById(id);

        if (empleado.isPresent()) {
            return ResponseEntity.ok(empleado.get()); // 🔹 devuelve el objeto dentro
        } else {
            return ResponseEntity.notFound().build(); // 🔹 404 si no existe
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<EmpleadoModel> updateEmpleadoById(@RequestBody EmpleadoModel request, @PathVariable("id") Long id){
        try {
            System.out.println("=== ACTUALIZANDO EMPLEADO ===");
            System.out.println("ID: " + id);
            System.out.println("Datos recibidos: " + request);

            EmpleadoModel updatedEmpleado = this.empleadoService.updateById(request, id);

            System.out.println("Empleado actualizado exitosamente");
            return ResponseEntity.ok(updatedEmpleado);
        } catch (Exception e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            e.printStackTrace();
            // Dejar que el GlobalExceptionHandler maneje la excepción
            throw e;
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmpleadoById(@PathVariable("id") Long id) {
        try {
            System.out.println("=== ELIMINANDO EMPLEADO ===");
            System.out.println("ID: " + id);

            boolean ok = this.empleadoService.deleteEmpleado(id);

            Map<String, Object> response = new HashMap<>();

            if(ok){
                response.put("message", "Empleado con ID " + id + " ha sido eliminado exitosamente");
                response.put("success", true);
                response.put("id", id);
                System.out.println("Empleado eliminado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "No se pudo eliminar el empleado con ID " + id);
                response.put("success", false);
                response.put("id", id);
                System.out.println("No se pudo eliminar el empleado");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}