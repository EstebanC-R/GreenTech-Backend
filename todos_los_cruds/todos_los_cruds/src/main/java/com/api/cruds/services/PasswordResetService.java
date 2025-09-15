package com.api.cruds.services;

import com.api.cruds.models.PasswordResetToken;
import com.api.cruds.models.UserModel;
import com.api.cruds.repositories.IUserRepository;
import com.api.cruds.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void initiatePasswordReset(String email) {
        // Verificar que el usuario existe
        Optional<UserModel> userOpt = userRepository.findById(email);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Limpiar tokens anteriores del usuario
        tokenRepository.markAllTokensAsUsedForEmail(email);

        // Generar nuevo token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // 1 hora de validez

        // Guardar token
        PasswordResetToken resetToken = new PasswordResetToken(email, token, expiryDate);
        tokenRepository.save(resetToken);

        // Enviar email
        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Buscar token válido
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);

        if (!tokenOpt.isPresent()) {
            throw new RuntimeException("Token inválido o ya utilizado");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Verificar que no esté expirado
        if (resetToken.isExpired()) {
            throw new RuntimeException("El token ha expirado");
        }

        // Buscar usuario
        Optional<UserModel> userOpt = userRepository.findById(resetToken.getEmail());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Actualizar contraseña
        UserModel user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    public void validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);

        if (!tokenOpt.isPresent()) {
            throw new RuntimeException("Token inválido o ya utilizado");
        }

        if (tokenOpt.get().isExpired()) {
            throw new RuntimeException("El token ha expirado");
        }
    }

    // Metodo para limpiar tokens expirados (ejecutar periódicamente)
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}