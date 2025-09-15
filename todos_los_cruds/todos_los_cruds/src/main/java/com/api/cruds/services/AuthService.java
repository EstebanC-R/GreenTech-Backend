package com.api.cruds.services;

import com.api.cruds.dto.LoginRequest;
import com.api.cruds.dto.LoginResponse;
import com.api.cruds.dto.UserProfileResponse;
import com.api.cruds.models.UserModel;
import com.api.cruds.repositories.IUserRepository;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Optional<UserModel> userOptional = iUserRepository.findByEmail(loginRequest.getEmail());

            if (userOptional.isPresent()) {
                UserModel user = userOptional.get();

                // Verificar contraseña
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                    // Verificar estado del usuario
                    if ("ACTIVO".equals(user.getEstado())) {
                        String token = jwtUtil.generateToken(user.getEmail(), user.getRol());
                        return new LoginResponse("Login exitoso", token, user.getRol(), user.getEmail());
                    } else {
                        return new LoginResponse("Usuario inactivo", null, null, null);
                    }
                } else {
                    return new LoginResponse("Credenciales inválidas", null, null, null);
                }
            } else {
                return new LoginResponse("Usuario no encontrado", null, null, null);
            }
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            return new LoginResponse("Error interno del servidor", null, null, null);
        }
    }

    public UserProfileResponse getUserProfile(String email) {
        System.out.println("DEBUG: Buscando perfil para email: " + email);

        try {
            // Usar la query personalizada de la vista
            Optional<Object[]> userDataOptional = iUserRepository.findUserProfileByEmail(email);

            if (userDataOptional.isPresent()) {
                Object userData = userDataOptional.get();

                System.out.println("DEBUG: Tipo de dato recibido: " + userData.getClass().getName());

                // Verificar si es un array anidado
                if (userData instanceof Object[]) {
                    Object[] dataArray = (Object[]) userData;
                    System.out.println("DEBUG: Array length: " + dataArray.length);

                    for (int i = 0; i < dataArray.length; i++) {
                        System.out.println("  [" + i + "]: " + dataArray[i] + " (Tipo: " +
                                (dataArray[i] != null ? dataArray[i].getClass().getSimpleName() : "null") + ")");
                    }

                    // Verificar que tenemos suficientes elementos
                    if (dataArray.length >= 6) {
                        UserProfileResponse response = new UserProfileResponse(
                                "Perfil obtenido exitosamente",
                                dataArray[0] != null ? dataArray[0].toString() : null,  // usuario_email
                                dataArray[1] != null ? dataArray[1].toString() : null,  // rol_usuario
                                (Date) dataArray[5],    // FECHA_INGRESO
                                dataArray[2] != null ? dataArray[2].toString() : null,  // ESTADO
                                dataArray[3] != null ? dataArray[3].toString() : null,  // admin_asignado
                                dataArray[4] != null ? dataArray[4].toString() : null   // admin_efectivo
                        );

                        System.out.println("DEBUG: Respuesta creada exitosamente para: " + response.getEmail());
                        return response;
                    } else {
                        System.out.println("DEBUG: Array insuficiente, length: " + dataArray.length);
                    }
                } else {
                    System.out.println("DEBUG: Tipo de dato no esperado: " + userData.getClass().getName());
                }

            }

            System.out.println("DEBUG: No se encontraron datos en la vista, intentando fallback");

            // Fallback: buscar usuario básico si la vista no tiene datos
            Optional<UserModel> basicUser = iUserRepository.findByEmail(email);
            if (basicUser.isPresent()) {
                UserModel user = basicUser.get();
                System.out.println("DEBUG: Usando datos básicos del usuario");

                return new UserProfileResponse(
                        "Perfil básico obtenido",
                        user.getEmail(),
                        user.getRol(),
                        user.getFechaIngreso(),
                        user.getEstado(),
                        null, // admin_asignado no disponible en tabla básica
                        null  // admin_efectivo no disponible en tabla básica
                );
            }

            return new UserProfileResponse(
                    "Usuario no encontrado",
                    null, null, null, null, null, null
            );

        } catch (ClassCastException e) {
            System.err.println("ERROR de casting en getUserProfile: " + e.getMessage());
            e.printStackTrace();

            // Intentar fallback inmediatamente
            try {
                Optional<UserModel> basicUser = iUserRepository.findByEmail(email);
                if (basicUser.isPresent()) {
                    UserModel user = basicUser.get();
                    System.out.println("DEBUG: Usando fallback después de error de casting");

                    return new UserProfileResponse(
                            "Perfil básico obtenido (fallback)",
                            user.getEmail(),
                            user.getRol(),
                            user.getFechaIngreso(),
                            user.getEstado(),
                            null,
                            null
                    );
                }
            } catch (Exception fallbackError) {
                System.err.println("ERROR en fallback: " + fallbackError.getMessage());
            }

            return new UserProfileResponse(
                    "Error de formato en datos del perfil",
                    null, null, null, null, null, null
            );
        } catch (Exception e) {
            System.err.println("ERROR general en getUserProfile: " + e.getMessage());
            e.printStackTrace();
            return new UserProfileResponse(
                    "Error al obtener perfil: " + e.getMessage(),
                    null, null, null, null, null, null
            );
        }
    }
}