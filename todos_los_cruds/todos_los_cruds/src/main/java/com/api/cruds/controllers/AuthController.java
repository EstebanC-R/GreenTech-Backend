package com.api.cruds.controllers;

import com.api.cruds.dto.LoginRequest;
import com.api.cruds.dto.LoginResponse;
import com.api.cruds.dto.UserProfileResponse;
import com.api.cruds.services.AuthService;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.cruds.dto.ForgotPasswordRequest;
import com.api.cruds.dto.ResetPasswordRequest;
import com.api.cruds.dto.ApiResponse;
import com.api.cruds.services.PasswordResetService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header "Bearer token"
            String token = authHeader.substring(7);

            // Validar el token
            String email = jwtUtil.extractEmail(token);
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(new UserProfileResponse("Token expirado", null, null, null, null, null, null));
            }

            // Obtener datos del usuario
            UserProfileResponse profile = authService.getUserProfile(email);
            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new UserProfileResponse("Token inválido", null, null, null, null, null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header "Bearer token"
            String token = authHeader.substring(7);

            // Validar que el token sea válido antes de hacer logout
            if (!jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.ok("Logout exitoso");
            } else {
                return ResponseEntity.status(401).body("Token expirado");
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }

    // MÉTODOS NUEVOS - Funcionalidad de recuperación de contraseña
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse("Se ha enviado un email con las instrucciones para restablecer tu contraseña", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Error interno del servidor", false));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Contraseña actualizada exitosamente", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Error interno del servidor", false));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse> validateResetToken(@RequestParam String token) {
        try {
            passwordResetService.validateToken(token);
            return ResponseEntity.ok(new ApiResponse("Token válido", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

}