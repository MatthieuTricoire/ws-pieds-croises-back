package com.crossfit.pieds_croises.controller;

import com.crossfit.pieds_croises.model.User;
import com.crossfit.pieds_croises.repository.UserRepository;
import com.crossfit.pieds_croises.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserRegistrationTest {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationTest.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private EmailService emailService;

    @BeforeEach
    void loadTestData() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("data-test.sql")
        );
        populator.execute(dataSource);
    }

    @BeforeEach
    void setupMocks() {
        doNothing().when(emailService).sendTemplateEmail(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testCreateUser() throws Exception {
        String json = """
                {
                    "firstname": "John",
                    "lastname": "Doe",
                    "email": "john.doe@example.com",
                    "roles": ["ROLE_USER"]
                }
                """;
        log.debug("Requête envoyée : {}", json);
        // requête POST pour créer un utilisateur
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("John"));

        // Vérifie que l'utilisateur est bien persisté
        Optional<User> savedUser = userRepository.findByEmail("john.doe@example.com");
        assertTrue(savedUser.isPresent(), "L'utilisateur doit exister en base");
        assertEquals("John", savedUser.get().getFirstname());
        assertEquals("Doe", savedUser.get().getLastname());
        log.info("Utilisateur persisté : {}", savedUser.get().getEmail());

        // Vérifie que l'email a bien été envoyé (mocké)
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(emailService, times(1))
                .sendTemplateEmail(any(), any(), any(), captor.capture());

        Map<String, Object> variables = captor.getValue();
        String registrationLink = (String) variables.get("registrationEmailLink");

        log.info("Invitation link: {}", registrationLink);

        // Récupère le lien avec le token et le prénom
        assertNotNull(registrationLink);
        assertTrue(registrationLink.contains("token="));
        assertTrue(registrationLink.contains("John"));

        log.info("Test terminé avec succès");
    }
}

