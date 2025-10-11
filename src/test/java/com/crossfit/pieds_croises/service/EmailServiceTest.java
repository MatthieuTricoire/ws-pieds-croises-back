package com.crossfit.pieds_croises.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = EmailService.class)
class EmailServiceTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @MockitoBean
    private TemplateEngine templateEngine;

    @Autowired
    private EmailService emailService;

    @Test
    void testGenerateInvitationLink_withUsername() {
        String link = emailService.generateInvitationLink("http://localhost:8080/invite", "token123", "user1");
        assertThat(link).isEqualTo("http://localhost:8080/invite?token=token123&username=user1");
    }

    @Test
    void testGenerateInvitationLink_withoutUsername() {
        String link = emailService.generateInvitationLink("http://localhost:8080/invite", "token123");
        assertThat(link).isEqualTo("http://localhost:8080/invite?token=token123");
    }

    @Test
    void testSendHtmlEmail_sendsEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlEmail("test@example.com", "Subject", "<h1>Hello</h1>");

        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendTemplateEmail_processesTemplateAndSends() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simuler le rendu du template
        when(templateEngine.process(eq("email/test-template"), any(Context.class))).thenReturn("<h1>Rendered</h1>");

        emailService.sendTemplateEmail("test@example.com", "Subject", "test-template", Map.of("name", "John"));

        // Vérifier que le template a été appelé
        verify(templateEngine, times(1)).process(eq("email/test-template"), any(Context.class));
        // Vérifier que l'email a été envoyé
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

}
