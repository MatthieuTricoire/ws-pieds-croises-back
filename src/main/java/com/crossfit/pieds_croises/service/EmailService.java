package com.crossfit.pieds_croises.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendInvitationEmail(String to, String invitationLink) {
        String subject = "Votre accès à la plateforme CrossFit Pieds Croisés";
        String content = buildHtmlContent(invitationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true pour HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Échec de l'envoi de l'email à " + to, e);
        }
    }

    private String buildHtmlContent(String link) {
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
}
