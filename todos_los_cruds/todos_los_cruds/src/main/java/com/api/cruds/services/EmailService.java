package com.api.cruds.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Restablecer Contraseña - GreenTech");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            String emailBody = "Hola,\n\n" +
                    "Has solicitado restablecer tu contraseña en GreenTech.\n\n" +
                    "Haz clic en el siguiente enlace para restablecer tu contraseña:\n" +
                    resetUrl + "\n\n" +
                    "Este enlace expirará en 1 hora por seguridad.\n\n" +
                    "Si no solicitaste este cambio, puedes ignorar este email.\n\n" +
                    "Saludos,\n" +
                    "Equipo GreenTech";

            message.setText(emailBody);
            message.setFrom("noreply@greentech.com");

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el email: " + e.getMessage());
        }
    }
}