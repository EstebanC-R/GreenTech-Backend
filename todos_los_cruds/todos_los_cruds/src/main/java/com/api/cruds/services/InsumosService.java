package com.api.cruds.services;

import com.api.cruds.models.InsumosModel;
import com.api.cruds.repositories.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class InsumosService {
    @Autowired
    InsumoRepository insumoRepository;

    // Obtener todos los insumos filtrados por acceso de usuario
    public ArrayList<InsumosModel> getInsumos(String userEmail) {
        System.out.println("Buscando insumos para usuario: " + userEmail);
        ArrayList<InsumosModel> insumos = insumoRepository.findInsumosByUserAccess(userEmail);
        System.out.println("Encontrados " + insumos.size() + " insumos");
        return insumos;
    }

    // Obtener insumos con filtros opcionales
    public ArrayList<InsumosModel> getInsumosWithFilters(String userEmail, String producto,
                                                         String proveedor, LocalDate fechaDesde,
                                                         LocalDate fechaHasta) {
        System.out.println("Buscando insumos con filtros para usuario: " + userEmail);
        ArrayList<InsumosModel> insumos = insumoRepository.findInsumosWithFilters(
                userEmail, producto, proveedor, fechaDesde, fechaHasta);
        System.out.println("Encontrados " + insumos.size() + " insumos con filtros");
        return insumos;
    }

    // Guardar nuevo insumo con verificación de permisos (solo administradores)
    public InsumosModel saveInsumo(InsumosModel datos, String userEmail) {
        System.out.println("Verificando permisos de administrador para usuario: " + userEmail);

        // Verificar que el usuario sea administrador
        if (!hasAdminAccess(userEmail)) {
            System.err.println("Usuario sin permisos de administrador: " + userEmail);
            throw new RuntimeException("Solo administradores pueden crear insumos");
        }

        System.out.println("Usuario administrador confirmado: " + userEmail);

        // Asignar el email del usuario (propietario del insumo)
        datos.setUserEmail(userEmail);

        System.out.println("Guardando insumo: " + datos.getProducto() + " para usuario: " + userEmail);
        InsumosModel saved = insumoRepository.save(datos);
        System.out.println("Insumo guardado con ID: " + saved.getId());

        return saved;
    }

    // Obtener insumo por ID con verificación de acceso
    public Optional<InsumosModel> getInsumoById(Integer id, String userEmail) {
        System.out.println("Buscando insumo ID: " + id + " para usuario: " + userEmail);
        return insumoRepository.findByIdWithUserAccess(id, userEmail);
    }

    // Actualizar insumo por ID con verificación de acceso (solo admin propietario)
    public InsumosModel updateById(InsumosModel request, Integer id, String userEmail) {
        System.out.println("Actualizando insumo ID: " + id + " para usuario: " + userEmail);

        // Verificar si el usuario puede modificar este insumo (debe ser admin propietario)
        if (!canModifyInsumo(id, userEmail)) {
            System.err.println("Sin permisos para modificar insumo - ID: " + id + ", Usuario: " + userEmail);
            throw new RuntimeException("No tiene permisos para modificar este insumo. Solo el administrador propietario puede hacerlo.");
        }

        // Buscar el insumo
        Optional<InsumosModel> insumoOpt = insumoRepository.findByIdWithUserAccess(id, userEmail);
        if (insumoOpt.isEmpty()) {
            System.err.println("Insumo no encontrado - ID: " + id);
            throw new RuntimeException("Insumo no encontrado");
        }

        InsumosModel datos = insumoOpt.get();

        // Actualizar campos
        datos.setProducto(request.getProducto());
        datos.setCantidadUsada(request.getCantidadUsada());
        datos.setMedida(request.getMedida());
        datos.setFechaDeUso(request.getFechaDeUso());
        datos.setCosto(request.getCosto());
        datos.setProveedor(request.getProveedor());

        // NO actualizar userEmail - debe mantenerse el propietario original

        InsumosModel updated = insumoRepository.save(datos);
        System.out.println("Insumo actualizado exitosamente");
        return updated;
    }

    // Eliminar insumo con verificación de acceso (solo admin propietario)
    public Boolean deleteInsumo(Integer id, String userEmail) {
        try {
            System.out.println("Intentando eliminar insumo ID: " + id + " para usuario: " + userEmail);

            // Verificar si el usuario puede eliminar este insumo (debe ser admin propietario)
            if (!canModifyInsumo(id, userEmail)) {
                System.err.println("Sin permisos para eliminar insumo - ID: " + id + ", Usuario: " + userEmail);
                return false;
            }

            insumoRepository.deleteById(id);
            System.out.println("Insumo eliminado exitosamente - ID: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar insumo ID: " + id + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Verificar acceso de administrador (para crear insumos)
    private boolean hasAdminAccess(String userEmail) {
        Long count = insumoRepository.countAdminAccess(userEmail);
        boolean access = count != null && count > 0;
        System.out.println("Verificación de acceso admin para " + userEmail + ": count=" + count + ", access=" + access);
        return access;
    }

    // Verificar acceso general del usuario (para leer insumos)
    private boolean hasUserAccess(String userEmail) {
        Long count = insumoRepository.countUserAccess(userEmail);
        boolean access = count != null && count > 0;
        System.out.println("Verificación de acceso usuario para " + userEmail + ": count=" + count + ", access=" + access);
        return access;
    }

    // Verificar si el usuario puede modificar/eliminar un insumo específico
    private boolean canModifyInsumo(Integer id, String userEmail) {
        Long count = insumoRepository.countByIdWithAdminAccess(id, userEmail);
        boolean canModify = count != null && count > 0;
        System.out.println("Verificación de permisos de modificación para insumo " + id + ", usuario " + userEmail + ": " + canModify);
        return canModify;
    }
}
