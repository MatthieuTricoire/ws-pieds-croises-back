package com.crossfit.pieds_croises.service;

import com.crossfit.pieds_croises.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailSendingException("Erreur lors de l'envoi de l'e-mail à " + to, e);
        }
    }

    public void sendInvitationEmail(String to, String invitationLink) {
        String subject = "Votre accès à la plateforme CrossFit Pieds Croisés";
        String content = buildInvitationHtmlContent(invitationLink);
        sendHtmlEmail(to, subject, content);
    }

    private String buildInvitationHtmlContent(String link) {
        return """
                <html>
                    <body>
                        <h2>Bienvenue sur CrossFit Pieds Croisés !</h2>
                        <p>Vous avez été invité à rejoindre notre plateforme.</p>
                        <p>Cliquez sur le bouton ci-dessous pour définir votre mot de passe et activer votre compte :</p>
                        <p style="margin-top: 20px;">
                            <a href="%s" style="padding: 10px 20px; background-color: #2d6cdf; color: white; text-decoration: none; border-radius: 5px;">
                                Activer mon compte
                            </a>
                        </p>
                        <p style="margin-top: 20px; font-size: 0.9em;">Ce lien est valable pendant 48 heures.</p>
                        <br/>
                        <p>Sportivement,<br/>L'équipe CrossFit Pieds Croisés</p>
                    </body>
                </html>
                """.formatted(link);
    }

    public String generateInvitationLink(String baseUrl, String token) {
        try {
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
            return baseUrl + "?token=" + encodedToken;
        } catch (Exception e) {
            logger.error("Error in EmailService", e);
            return null;
        }
    }
}
