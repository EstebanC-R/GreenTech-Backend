package com.api.cruds.repositories;

import com.api.cruds.models.ObservationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface IObservationRepository extends JpaRepository<ObservationModel, Integer> {

    // Obtener observaciones con acceso de usuario
    @Query(value = """
        SELECT o.* FROM datos_observaciones o
        WHERE EXISTS (
            SELECT 1 FROM vista_usuarios_con_admin v 
            WHERE v.usuario_email = :userEmail AND v.admin_efectivo IS NOT NULL
        )
        ORDER BY o.fecha_publicacion DESC
        """, nativeQuery = true)
    ArrayList<ObservationModel> findObservationsByUserAccess(@Param("userEmail") String userEmail);

    // CORREGIDA: Verificar acceso de usuario - retornar Long y comparar en Java
    @Query(value = """
        SELECT COUNT(*)
        FROM vista_usuarios_con_admin v 
        WHERE v.usuario_email = :userEmail AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Long countUserAccess(@Param("userEmail") String userEmail);

    // Buscar observación por ID con verificación de acceso
    @Query(value = """
        SELECT o.* FROM datos_observaciones o
        WHERE o.id = :id 
        AND EXISTS (
            SELECT 1 FROM vista_usuarios_con_admin v 
            WHERE v.usuario_email = :userEmail AND v.admin_efectivo IS NOT NULL
        )
        """, nativeQuery = true)
    Optional<ObservationModel> findByIdWithUserAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);

    // CORREGIDA: Verificar si una observación específica existe y el usuario tiene acceso
    @Query(value = """
        SELECT COUNT(*)
        FROM datos_observaciones o
        WHERE o.id = :id 
        AND EXISTS (
            SELECT 1 FROM vista_usuarios_con_admin v 
            WHERE v.usuario_email = :userEmail AND v.admin_efectivo IS NOT NULL
        )
        """, nativeQuery = true)
    Long countByIdAndUserAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);

    // Método para eliminar con verificación previa - CORREGIDA para retornar Long
    @Query(value = """
        SELECT COUNT(*)
        FROM datos_observaciones o
        WHERE o.id = :id 
        AND EXISTS (
            SELECT 1 FROM vista_usuarios_con_admin v 
            WHERE v.usuario_email = :userEmail AND v.admin_efectivo IS NOT NULL
        )
        """, nativeQuery = true)
    Long countByIdWithUserAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);
}