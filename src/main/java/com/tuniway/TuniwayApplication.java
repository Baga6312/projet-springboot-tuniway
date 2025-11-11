package com.tuniway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TuniwayApplication {

    public static void main(String[] args) {
        // For frontend-only mode, exclude database auto-configurations
        // This is handled via application.properties exclusions
        SpringApplication.run(TuniwayApplication.class, args);
    }
}