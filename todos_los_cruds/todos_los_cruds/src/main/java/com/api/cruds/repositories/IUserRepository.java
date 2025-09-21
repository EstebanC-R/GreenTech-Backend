package com.api.cruds.repositories;

import com.api.cruds.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, String> {

    default Optional<UserModel> findByEmail(String email) {
        return findById(email);
    }

    @Query(value = """
        SELECT 
            v.usuario_email,
            v.rol_usuario, 
            v.estado,       
            v.admin_asignado,
            v.admin_efectivo,
            v.fecha_ingreso  
        FROM vista_usuarios_con_admin v
        JOIN usuarios u ON v.usuario_email = u.id_usuario
        WHERE v.usuario_email = :email
    """, nativeQuery = true)
    Optional<Object[]> findUserProfileByEmail(@Param("email") String email);
}