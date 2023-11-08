package com.leeuw.prof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.leeuw.entities")
@ComponentScan(basePackages = {"com.leeuw.controllers","com.leeuw.services"})
@EnableJpaRepositories(basePackages = "com.leeuw.repositories")
public class ProfApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfApplication.class, args);
    }

}
