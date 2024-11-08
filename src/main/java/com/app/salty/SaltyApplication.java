package com.app.salty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.app.salty")
public class SaltyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaltyApplication.class, args);
    }

}
