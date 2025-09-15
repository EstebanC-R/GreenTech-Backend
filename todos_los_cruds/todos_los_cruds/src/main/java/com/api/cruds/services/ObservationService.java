package com.api.cruds.services;

import com.api.cruds.models.ObservationModel;
import com.api.cruds.repositories.IObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ObservationService {
    @Autowired
    IObservationRepository observationRepository;

    // Obtener todas las observaciones filtradas por acceso de usuario
    public ArrayList<ObservationModel> getObservation(String userEmail) {
        System.out.println("Buscando observaciones para usuario: " + userEmail);
        ArrayList<ObservationModel> observations = observationRepository.findObservationsByUserAccess(userEmail);
        System.out.println("Encontradas " + observations.size() + " observaciones");
        return observations;
    }

    // Guardar nueva observación con verificación de permisos
    public ObservationModel saveObservation(ObservationModel datos, String userEmail) {
        System.out.println("Verificando permisos para usuario: " + userEmail);

        // Verificar que el usuario tenga permisos
        if (!hasAccess(userEmail)) {
            System.err.println("Usuario sin permisos: " + userEmail);
            throw new RuntimeException("No tiene permisos para crear observaciones");
        }

        System.out.println("Usuario con permisos confirmados: " + userEmail);

        // Asignar fecha actual si no viene especificada
        if (datos.getFecha_publicacion() == null) {
            datos.setFecha_publicacion(LocalDateTime.now());
            System.out.println("Fecha asignada automáticamente: " + datos.getFecha_publicacion());
        }

        System.out.println("Guardando observación: " + datos.getTitulo());
        ObservationModel saved = observationRepository.save(datos);
        System.out.println("Observación guardada con ID: " + saved.getId());

        return saved;
    }

    // Obtener observación por ID con verificación de acceso
    public Optional<ObservationModel> getObservationById(Integer id, String userEmail) {
        System.out.println("Buscando observación ID: " + id + " para usuario: " + userEmail);
        return observationRepository.findByIdWithUserAccess(id, userEmail);
    }

    // Actualizar observación por ID con verificación de acceso
    public ObservationModel updateById(ObservationModel request, Integer id, String userEmail) {
        System.out.println("Actualizando observación ID: " + id + " para usuario: " + userEmail);

        // Buscar la observación con verificación de acceso
        Optional<ObservationModel> observationOpt = observationRepository.findByIdWithUserAccess(id, userEmail);

        if (observationOpt.isEmpty()) {
            System.err.println("Observación no encontrada o sin permisos - ID: " + id + ", Usuario: " + userEmail);
            throw new RuntimeException("Observación no encontrada o no tiene permisos para actualizarla");
        }

        ObservationModel datos = observationOpt.get();
        datos.setTitulo(request.getTitulo());
        datos.setNombre(request.getNombre());
        datos.setDescripcion(request.getDescripcion());

        // Solo actualizar fecha si viene en el request (normalmente no debería cambiar)
        if (request.getFecha_publicacion() != null) {
            datos.setFecha_publicacion(request.getFecha_publicacion());
        }

        ObservationModel updated = observationRepository.save(datos);
        System.out.println("Observación actualizada exitosamente");
        return updated;
    }

    // Eliminar observación con verificación de acceso
    public Boolean deleteObservation(Integer id, String userEmail) {
        try {
            System.out.println("Intentando eliminar observación ID: " + id + " para usuario: " + userEmail);

            // Verificar si existe la observación y el usuario tiene acceso
            Long count = observationRepository.countByIdWithUserAccess(id, userEmail);
            if (count == null || count == 0) {
                System.err.println("Observación no encontrada o sin permisos - ID: " + id);
                return false;
            }

            observationRepository.deleteById(id);
            System.out.println("Observación eliminada exitosamente - ID: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar observación ID: " + id + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Verificar acceso del usuario
    private boolean hasAccess(String userEmail) {
        Long count = observationRepository.countUserAccess(userEmail);
        boolean access = count != null && count > 0;
        System.out.println("Verificación de acceso para " + userEmail + ": count=" + count + ", access=" + access);
        return access;
    }
}