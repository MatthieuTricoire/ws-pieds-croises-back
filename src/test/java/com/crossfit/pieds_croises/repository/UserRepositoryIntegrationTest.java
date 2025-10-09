package com.crossfit.pieds_croises.repository;

import com.crossfit.pieds_croises.model.Course;
import com.crossfit.pieds_croises.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class UserRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.1")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
    }

    @BeforeEach
    void loadTestData() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("data-test.sql")
        );
        populator.execute(dataSource);
    }

    @Test
    void shouldFindUserByEmail() {
        Optional<User> found = userRepository.findByEmail("coach@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("coach@example.com");
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        assertThat(userRepository.existsByEmail("jean.dupont@example.com")).isTrue();
    }

    @Test
    void shouldFindUsersNotInCourse() {
        User coach = userRepository.findByEmail("coach@example.com").orElseThrow();

        Course course = courseRepository.findAll().stream()
                .filter(c -> c.getCoach().getEmail().equals("coach@example.com"))
                .findFirst()
                .orElseThrow();

        List<User> result = userRepository.findAllUsersNotInCourse(course, coach);

        assertThat(result)
                .extracting(User::getEmail)
                .contains("admin@example.com");
    }

    @Test
    void shouldFindAllWithUserSubscriptions() {
        List<User> users = userRepository.findAllWithUserSubscriptions();
        assertThat(users).isNotNull();
    }
}
