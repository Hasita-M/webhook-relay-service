package com.codewithhasita.webrelayretryservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class WebRelayRetryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebRelayRetryServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner seedTestReceiver(ReceiverRepository receiverRepository, UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User u = new User();
                u.setEmail("test@test.com");
                u.setName("Test User");
                u.setPasswordHash("placeholder-hash");
                u.setCreatedAt(LocalDateTime.now());
                u = userRepository.save(u);

                Receiver r = new Receiver();
                r.setDestinationURL("http://localhost:8080/receiver");
                r.setName("Test Receiver");
                r.setSecretKey("placeholder-secret");
                r.setUser(u);
                receiverRepository.save(r);
            }
        };
    }
}
