package com.api.cruds.repositories;

import com.api.cruds.models.InsumosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<InsumosModel, Integer> {

    // Obtener insumos con acceso de usuario (consulta universal)
    @Query(value = """
        SELECT DISTINCT i.* 
        FROM datos_insumos i
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        ORDER BY i.fecha_de_uso DESC, i.created_at DESC
        """, nativeQuery = true)
    ArrayList<InsumosModel> findInsumosByUserAccess(@Param("userEmail") String userEmail);

    // Obtener insumos con filtros opcionales
    @Query(value = """
        SELECT DISTINCT i.* 
        FROM datos_insumos i
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
          AND (:producto IS NULL OR i.producto LIKE CONCAT('%', :producto, '%'))
          AND (:proveedor IS NULL OR i.proveedor LIKE CONCAT('%', :proveedor, '%'))
          AND (:fechaDesde IS NULL OR i.fecha_de_uso >= :fechaDesde)
          AND (:fechaHasta IS NULL OR i.fecha_de_uso <= :fechaHasta)
        ORDER BY i.fecha_de_uso DESC, i.created_at DESC
        """, nativeQuery = true)
    ArrayList<InsumosModel> findInsumosWithFilters(
            @Param("userEmail") String userEmail,
            @Param("producto") String producto,
            @Param("proveedor") String proveedor,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );

    // Verificar si usuario puede crear insumos (solo administradores)
    @Query(value = """
        SELECT COUNT(*)
        FROM vista_usuarios_con_admin v 
        WHERE v.usuario_email = :userEmail 
          AND v.rol_usuario = 'ADMINISTRADOR'
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Long countAdminAccess(@Param("userEmail") String userEmail);

    // Verificar acceso general del usuario (lectura)
    @Query(value = """
        SELECT COUNT(*)
        FROM vista_usuarios_con_admin v 
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Long countUserAccess(@Param("userEmail") String userEmail);

    // Buscar insumo por ID con verificación de acceso
    @Query(value = """
        SELECT DISTINCT i.* 
        FROM datos_insumos i
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
        )
        WHERE i.id_insumos = :id 
          AND v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Optional<InsumosModel> findByIdWithUserAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);

    // Verificar si el usuario puede modificar/eliminar un insumo específico (solo admin propietario)
    @Query(value = """
        SELECT COUNT(*)
        FROM datos_insumos i
        JOIN vista_usuarios_con_admin v ON (
            v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email
        )
        WHERE i.id_insumos = :id 
          AND v.usuario_email = :userEmail
        """, nativeQuery = true)
    Long countByIdWithAdminAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);

    // Verificar acceso de lectura a un insumo específico
    @Query(value = """
        SELECT COUNT(*)
        FROM datos_insumos i
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
        )
        WHERE i.id_insumos = :id 
          AND v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Long countByIdWithUserAccess(@Param("id") Integer id, @Param("userEmail") String userEmail);
}
