package org.avsytem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ReceitasApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceitasApiApplication.class, args);
    }
}
