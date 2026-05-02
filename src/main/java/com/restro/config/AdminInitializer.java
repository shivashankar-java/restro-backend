package com.restro.config;

import com.restro.entity.Role;
import com.restro.entity.User;
import com.restro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminInitializer.class);

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);

                // optional: leave address null OR set default
                admin.setAddress(null);

                userRepository.save(admin);

                logger.info("ADMIN user created successfully");
            } else {
                logger.info("ADMIN already exists, skipping initialization");
            }
        };
    }
}
