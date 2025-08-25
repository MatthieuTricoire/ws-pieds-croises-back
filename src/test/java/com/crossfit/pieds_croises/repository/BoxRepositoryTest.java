//package com.crossfit.pieds_croises.repository;
//
//import com.crossfit.pieds_croises.AbstractIntegrationTests;
//import com.crossfit.pieds_croises.model.Box;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.ActiveProfiles;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.Clock;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Testcontainers
////@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("test")
//public class BoxRepositoryTest extends AbstractIntegrationTests {
//
//    @Autowired
//    private BoxRepository boxRepository;
//
//    @Autowired
//    Environment env;
//
//    @Test
//    void printDatasourceUrl() throws Exception {
//        String url = env.getProperty("spring.datasource.url");
//        System.out.println("Datasource URL utilisée par le test : " + url);
//        System.out.println("Database Username: " + env.getProperty("spring.datasource.username"));
//    }
//
//    @Test
//    void printActiveProfiles() {
//        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
//    }
//
//    @Test
//    void checkInitialBoxes() {
//        List<Box> boxes = boxRepository.findAll();
//        System.out.println("Nombre initial de boxes en base: " + boxes.size());
//        boxes.forEach(box -> System.out.println(box.getName() + " / " + box.getId()));
//    }
//
//    @Test
//    void testFindAllBoxes() {
//        Clock fixedClock = Clock.fixed(Instant.parse("2025-07-10T13:45:00Z"), ZoneId.of("Europe/Paris"));
//        LocalDateTime now = LocalDateTime.now(fixedClock);
//
//        Box box1 = new Box();
//        box1.setName("Box 1");
//        box1.setZipcode("12345");
//        box1.setCreatedAt(now);
//        box1.setUpdatedAt(now);
//
//        Box box2 = new Box();
//        box2.setName("Box 2");
//        box2.setZipcode("67890");
//        box2.setCreatedAt(now);
//        box2.setUpdatedAt(now);
//
//        boxRepository.saveAll(List.of(box1, box2));
//
//        List<Box> boxes = boxRepository.findAll();
//
//        assertThat(boxes).hasSize(2);
//        assertThat(boxes.get(0).getName()).isEqualTo("Box 1");
//        assertThat(boxes.get(0).getZipcode()).isEqualTo("12345");
//        assertThat(boxes.get(0).getCreatedAt()).isEqualTo(now);
//        assertThat(boxes.get(0).getUpdatedAt()).isEqualTo(now);
//        assertThat(boxes.get(1).getName()).isEqualTo("Box 2");
//        assertThat(boxes.get(1).getZipcode()).isEqualTo("67890");
//        assertThat(boxes.get(1).getCreatedAt()).isEqualTo(now);
//        assertThat(boxes.get(1).getUpdatedAt()).isEqualTo(now);
//    }
//
//    @Test
//    void testFindBoxById() {
//        Clock fixedClock = Clock.fixed(Instant.parse("2025-07-10T13:45:00Z"), ZoneId.of("Europe/Paris"));
//        LocalDateTime now = LocalDateTime.now(fixedClock);
//
//        Box box = new Box();
//        box.setName("Test Box");
//        box.setZipcode("12345");
//        box.setCreatedAt(now);
//        box.setUpdatedAt(now);
//
//        Box savedBox = boxRepository.save(box);
//
//        Optional<Box> foundBoxOpt = boxRepository.findById(savedBox.getId());
//
//        assertThat(foundBoxOpt).isPresent();
//
//        Box foundBox = foundBoxOpt.get();
//        assertThat(foundBox.getName()).isEqualTo("Test Box");
//        assertThat(foundBox.getZipcode()).isEqualTo("12345");
//        assertThat(foundBox.getCreatedAt()).isEqualTo(now);
//        assertThat(foundBox.getUpdatedAt()).isEqualTo(now);
//    }
//}
