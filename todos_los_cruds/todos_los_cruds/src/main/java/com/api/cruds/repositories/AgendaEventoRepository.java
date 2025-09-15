package com.api.cruds.repositories;

import com.api.cruds.models.AgendaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaEventoRepository extends JpaRepository<AgendaEvento, Long> {

    /**
     * Obtener eventos que puede ver el usuario (con permisos)
     */
    @Query(value = """
        SELECT DISTINCT ae.*
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        ORDER BY ae.fecha_evento ASC
        """, nativeQuery = true)
    List<AgendaEvento> findEventosConPermisos(@Param("userEmail") String userEmail);

    /**
     * Buscar evento específico con permisos
     */
    @Query(value = """
        SELECT DISTINCT ae.*
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
        )
        WHERE ae.id_evento = :idEvento 
          AND v.usuario_email = :userEmail
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    Optional<AgendaEvento> findEventoConPermisos(@Param("idEvento") Long idEvento, @Param("userEmail") String userEmail);

    /**
     * Verificar si usuario puede editar evento (solo ADMINISTRADORES propietarios)
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON ae.user_email = v.usuario_email
        WHERE ae.id_evento = :idEvento 
          AND v.usuario_email = :userEmail
          AND v.rol_usuario = 'ADMINISTRADOR'
          AND ae.user_email = :userEmail
        """, nativeQuery = true)
    boolean puedeEditarEvento(@Param("idEvento") Long idEvento, @Param("userEmail") String userEmail);

    /**
     * Obtener admin efectivo del usuario (para crear eventos)
     */
    @Query(value = """
        SELECT v.admin_efectivo
        FROM vista_usuarios_con_admin v 
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        LIMIT 1
        """, nativeQuery = true)
    Optional<String> getAdminEfectivo(@Param("userEmail") String userEmail);

    /**
     * Estadísticas con permisos
     */
    @Query(value = """
        SELECT 
            COALESCE(COUNT(*), 0) as totalEventos,
            COALESCE(SUM(CASE WHEN ae.completado = FALSE THEN 1 ELSE 0 END), 0) as eventosPendientes,
            COALESCE(SUM(CASE WHEN ae.completado = TRUE THEN 1 ELSE 0 END), 0) as eventosCompletados,
            COALESCE(SUM(CASE WHEN DATE(ae.fecha_evento) = CURDATE() THEN 1 ELSE 0 END), 0) as eventosHoy
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
        """, nativeQuery = true)
    List<Object[]> getEstadisticasConPermisos(@Param("userEmail") String userEmail);

    /**
     * Eventos pendientes con permisos
     */
    @Query(value = """
        SELECT ae.*
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
          AND ae.completado = FALSE
        ORDER BY ae.fecha_evento ASC
        """, nativeQuery = true)
    List<AgendaEvento> findEventosPendientesConPermisos(@Param("userEmail") String userEmail);

    /**
     * Eventos de hoy con permisos
     */
    @Query(value = """
        SELECT ae.*
        FROM agenda_eventos ae
        JOIN vista_usuarios_con_admin v ON (
            (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
            OR
            (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
        )
        WHERE v.usuario_email = :userEmail 
          AND v.admin_efectivo IS NOT NULL
          AND DATE(ae.fecha_evento) = CURDATE()
        ORDER BY ae.fecha_evento ASC
        """, nativeQuery = true)
    List<AgendaEvento> findEventosHoyConPermisos(@Param("userEmail") String userEmail);
}