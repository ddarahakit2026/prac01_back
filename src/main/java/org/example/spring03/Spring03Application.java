package org.example.spring03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.AuthenticationFilter;

@SpringBootApplication
public class Spring03Application {

    public static void main(String[] args) {

        SpringApplication.run(Spring03Application.class, args);
    }

}
