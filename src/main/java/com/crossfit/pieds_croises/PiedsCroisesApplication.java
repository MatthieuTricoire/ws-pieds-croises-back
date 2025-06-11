package com.crossfit.pieds_croises;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
public class PiedsCroisesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiedsCroisesApplication.class, args);
    }

    @Bean
    CommandLineRunner runDataSql(DataSource dataSource) {
        return args -> {
            ResourceDatabasePopulator resourceDatabasePopulator =
                    new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("data.sql"));
            resourceDatabasePopulator.execute(dataSource);
        };
    }

}
