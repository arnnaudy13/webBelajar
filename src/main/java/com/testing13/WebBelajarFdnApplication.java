package com.testing13;

import com.testing13.entity.Role;
import com.testing13.entity.User;
import com.testing13.repository.RoleRepository;
import com.testing13.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class WebBelajarFdnApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebBelajarFdnApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmailIgnoreCase("arnnaudy123@gmail.com").isEmpty()) {
                // Check if ADMIN role exists or create it
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

                // Create admin user with hashed password
                User admin = User.builder()
                        .email("arnnaudy123@gmail.com")
                        .password(passwordEncoder.encode("admin123"))
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
                System.out.println("✅ Admin user created!");
            }
        };
    }
}
