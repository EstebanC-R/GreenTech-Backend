package com.api.cruds.repositories;

import com.api.cruds.models.EmpleadoModel;
import com.api.cruds.models.EstadoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEmpleadosRepository extends JpaRepository<EmpleadoModel, Long> {  // ← Cambié Integer por Long

    // Buscar por nombre o cédula y filtrar por estado
    @Query("SELECT e FROM EmpleadoModel e WHERE " +
            "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:cedula IS NULL OR e.cedula LIKE %:cedula%) AND " +
            "(:estado IS NULL OR e.estado = :estado)")
    List<EmpleadoModel> buscarConFiltros(@Param("nombre") String nombre,
                                         @Param("cedula") String cedula,
                                         @Param("estado") EstadoEmpleado estado);

    // Métodos adicionales útiles para validaciones
    boolean existsByCedula(String cedula);
    boolean existsByCelular(String celular);
    boolean existsByCorreo(String correo);

    boolean existsByCedulaAndIdNot(String cedula, Long id);
    boolean existsByCelularAndIdNot(String celular, Long id);
    boolean existsByCorreoAndIdNot(String correo, Long id);

    Optional<EmpleadoModel> findByCedula(String cedula);
    Optional<EmpleadoModel> findByCelular(String celular);
    Optional<EmpleadoModel> findByCorreo(String correo);

    // Para filtrar por estado
    List<EmpleadoModel> findByEstado(EstadoEmpleado estado);
    List<EmpleadoModel> findByNombreContainingIgnoreCase(String nombre);
}