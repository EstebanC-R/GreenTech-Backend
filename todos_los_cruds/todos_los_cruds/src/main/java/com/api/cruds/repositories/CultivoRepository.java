package com.api.cruds.repositories;

import com.api.cruds.models.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CultivoRepository extends JpaRepository<Cultivo, Long> {

    // Buscar por usuario responsable
    List<Cultivo> findByUsuarioResponsable(String usuarioResponsable);

    // Buscar por estado
    List<Cultivo> findByEstadoCultivo(Cultivo.EstadoCultivo estado);

    // Buscar por device
    List<Cultivo> findByDeviceIdFk(Long deviceId);

    // Buscar por usuario y estado
    List<Cultivo> findByUsuarioResponsableAndEstadoCultivo(String usuarioResponsable, Cultivo.EstadoCultivo estado);

    // Consulta personalizada: cultivos con información de device
    @Query("""
        SELECT c FROM Cultivo c 
        LEFT JOIN FETCH c.device d 
        WHERE c.usuarioResponsable = :usuarioEmail 
        ORDER BY c.createdAt DESC
        """)
    List<Cultivo> findCultivosConDeviceByUsuario(@Param("usuarioEmail") String usuarioEmail);

    // Para empleados: buscar cultivos de su admin asignado
    @Query(value = """
        SELECT c.* FROM CULTIVOS c
        JOIN vista_usuarios_con_admin v ON c.USUARIO_RESPONSABLE = v.admin_efectivo
        WHERE v.usuario_email = :empleadoEmail
        ORDER BY c.CREATED_AT DESC
        """, nativeQuery = true)
    List<Cultivo> findCultivosByEmpleado(@Param("empleadoEmail") String empleadoEmail);
}