package com.insurance.server.config;

import com.insurance.server.entity.User;
import com.insurance.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {

            if (userRepository.count() > 0) {
                System.out.println("ℹ️ Пользователи уже существуют, инициализация пропущена");
                return;
            }

            List<User> users = List.of(

                    createUser(
                            "admin@insurance.ru",
                            "password123",
                            "Петров Пётр Петрович",
                            User.UserRole.ROLE_ADMIN
                    ),

                    createUser(
                            "ivanov@insurance.ru",
                            "password123",
                            "Иванов Иван Иванович",
                            User.UserRole.ROLE_MANAGER
                    ),

                    createUser(
                            "sidorov@insurance.ru",
                            "password123",
                            "Сидоров Сидор Сидорович",
                            User.UserRole.ROLE_MANAGER
                    ),

                    createUser(
                            "smirnov@insurance.ru",
                            "password123",
                            "Смирнов Алексей Михайлович",
                            User.UserRole.ROLE_AGENT
                    ),

                    createUser(
                            "kozlov@mail.ru",
                            "password123",
                            "Козлов Андрей Викторович",
                            User.UserRole.ROLE_CLIENT
                    ),

                    createUser(
                            "novikov@mail.ru",
                            "password123",
                            "Новиков Дмитрий Сергеевич",
                            User.UserRole.ROLE_CLIENT
                    ),

                    createUser(
                            "fedorov@mail.ru",
                            "password123",
                            "Фёдоров Михаил Александрович",
                            User.UserRole.ROLE_CLIENT
                    )
            );

            userRepository.saveAll(users);

            System.out.println("✅ Создано 7 начальных пользователей");
        };
    }

    private User createUser(
            String email,
            String rawPassword,
            String fullName,
            User.UserRole role
    ) {
        User user = new User();
        user.setEmailAddress(email);
        user.setUserPassword(passwordEncoder.encode(rawPassword));
        user.setFullUserName(fullName);
        user.setUserRole(role);
        user.setAccountActive(true);
        return user;
    }
}
