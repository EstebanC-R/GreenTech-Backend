package com.api.cruds.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔐 Restablecer Contraseña - GreenTech");
            helper.setFrom("noreply@greentech.com");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            String htmlContent = createPasswordResetEmailTemplate(resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email: " + e.getMessage());
        }
    }

    private String createPasswordResetEmailTemplate(String resetUrl) {
        // Logo blanco de GreenTech
        String logoUrl = "https://i.ibb.co/Wv3qFYwW/logo-white-greentech-R.png";

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Restablecer Contraseña - GreenTech</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f5f5f5;
                    }
                    
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: white;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #2d3748, #4a5568);
                        padding: 30px;
                        text-align: center;
                        color: white;
                    }
                    
                    .logo {
                        display: flex;
                        align-items: center;
                        width: 100%%;
                        margin-bottom: 15px;
                    }
                    
                    .logo-text {
                        color: white;
                        font-size: 28px;
                        font-weight: bold;
                    }
                    
                    .logo img {
                        width: 40px;
                        height: 40px;
                        object-fit: contain;
                        margin-left: auto; /* empuja el logo a la derecha */
                    }
                    
                    .header-subtitle {
                        font-size: 16px;
                        opacity: 0.9;
                    }
                    
                    .content {
                        padding: 40px 30px;
                    }
                    
                    .greeting {
                        font-size: 24px;
                        color: #1f2937;
                        margin-bottom: 20px;
                        font-weight: 600;
                    }
                    
                    .message {
                        font-size: 16px;
                        color: #6b7280;
                        margin-bottom: 30px;
                        line-height: 1.7;
                    }
                    
                    .cta-container {
                        text-align: center;
                        margin: 35px 0;
                    }
                    
                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #4a5568, #2d3748);
                        color: white !important;
                        padding: 16px 32px;
                        text-decoration: none;
                        border-radius: 8px;
                        font-weight: 600;
                        font-size: 16px;
                        transition: all 0.3s ease;
                        box-shadow: 0 4px 15px rgba(74, 85, 104, 0.3);
                        border: 2px solid transparent;
                    }
                    
                    .cta-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(74, 85, 104, 0.4);
                        border-color: #10b981;
                    }
                    
                    .security-notice {
                        background: #f0f9ff;
                        border: 1px solid #0ea5e9;
                        border-radius: 8px;
                        padding: 20px;
                        margin: 25px 0;
                    }
                    
                    .security-notice .icon {
                        font-size: 20px;
                        margin-right: 10px;
                        color: #0ea5e9;
                    }
                    
                    .security-text {
                        color: #0c4a6e;
                        font-size: 14px;
                        font-weight: 500;
                    }
                    
                    .footer {
                        background: #f9fafb;
                        padding: 25px 30px;
                        border-top: 1px solid #e5e7eb;
                        text-align: center;
                    }
                    
                    .footer-text {
                        color: #6b7280;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                    
                    .signature {
                        color: #1f2937;
                        font-weight: 600;
                        font-size: 16px;
                    }
                    
                    .divider {
                        height: 1px;
                        background: linear-gradient(90deg, transparent, #e5e7eb, transparent);
                        margin: 25px 0;
                    }
                    
                    @media (max-width: 600px) {
                        .container {
                            margin: 10px;
                            border-radius: 8px;
                        }
                        
                        .content {
                            padding: 25px 20px;
                        }
                        
                        .header {
                            padding: 25px 20px;
                        }
                        
                        .cta-button {
                            padding: 14px 24px;
                            font-size: 15px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <!-- Header -->
                    <div class="header">
                        <div class="logo">
                            <span class="logo-text">GreenTech</span>
                            <img src="%s" alt="GreenTech Logo">
                        </div>
                        <div class="header-subtitle">Monitoreo de Cultivos</div>
                    </div>
                    
                    <!-- Content -->
                    <div class="content">
                        <div class="greeting">¡Hola!</div>
                        
                        <div class="message">
                            Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en GreenTech. 
                            Si fuiste tú quien realizó esta solicitud, puedes proceder haciendo clic en el botón de abajo.
                        </div>
                        
                        <div class="cta-container">
                            <a href="%s" class="cta-button">
                                🔐 Restablecer mi contraseña
                            </a>
                        </div>
                        
                        <div class="security-notice">
                            <div class="security-text">
                                <span class="icon">⏰</span>
                                <strong>Importante:</strong> Este enlace expirará en 1 hora por tu seguridad.
                            </div>
                        </div>
                        
                        <div class="divider"></div>
                        
                        <div class="message">
                            Si no solicitaste este cambio de contraseña, puedes ignorar este correo de forma segura. 
                            Tu cuenta permanecerá protegida.
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div class="footer">
                        <div class="footer-text">
                            ¿Tienes problemas con el botón? Copia y pega este enlace en tu navegador:
                            <br>
                            <a href="%s" style="color: #2d3748; word-break: break-all;">%s</a>
                        </div>
                        
                        <div class="signature">
                            Con cariño,<br>
                            <strong>El equipo de GreenTech</strong> 🌱
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(logoUrl, resetUrl, resetUrl, resetUrl);
    }
}
