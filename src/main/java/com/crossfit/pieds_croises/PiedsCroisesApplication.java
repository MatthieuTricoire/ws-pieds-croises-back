package com.crossfit.pieds_croises;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
public class PiedsCroisesApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().filename(".env.dev").ignoreIfMissing().ignoreIfMalformed().load();
    dotenv.entries().forEach(entry -> {
      System.setProperty(entry.getKey(), entry.getValue());
    });
    SpringApplication app = new SpringApplication(PiedsCroisesApplication.class);
    app.setAdditionalProfiles("dev");
    if (System.getProperty("spring.profiles.active") == null &&
        System.getenv("SPRING_PROFILES_ACTIVE") == null) {
      app.setAdditionalProfiles("dev");
    }
    app.run(args);
  }

  // @Bean
  // @Profile("!test")
  // CommandLineRunner runDataSql(DataSource dataSource) {
  // return args -> {
  // ResourceDatabasePopulator resourceDatabasePopulator =
  // new ResourceDatabasePopulator(false, false, "UTF-8", new
  // ClassPathResource("data.sql"));
  // resourceDatabasePopulator.execute(dataSource);
  // };
  // }

}
