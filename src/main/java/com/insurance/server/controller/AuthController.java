package com.insurance.server.controller;

import com.insurance.server.entity.User;
import com.insurance.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        log.info("Запрос на регистрацию пользователя: {}", user.getEmailAddress());

        try {
            User createdUser = userService.createUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Регистрация прошла успешно");
            response.put("userId", createdUser.getUserId());
            response.put("email", createdUser.getEmailAddress());
            response.put("role", createdUser.getUserRole().name());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Ошибка регистрации: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {

        String email = credentials.get("email");
        String password = credentials.get("password");

        log.info("Попытка входа для пользователя: {}", email);
        log.info("Полученный пароль: {}", password);
        log.info("Введённый пароль: {}", password);


        try {
            User user = userService.getUserByEmail(email);
            log.info("Хэш из БД: {}", user.getUserPassword());
            log.info("Результат matches(): {}", passwordEncoder.matches(password, user.getUserPassword()));
            log.info("Найден пользователь: {}", user.getEmailAddress());
            log.info("Хеш пароля в БД: {}", user.getUserPassword());  // ← Добавьте эту строку
            log.info("Сравнение пароля: {}", passwordEncoder.matches(password, user.getUserPassword()));

            if (passwordEncoder.matches(password, user.getUserPassword())) {

                if (!Boolean.TRUE.equals(user.getAccountActive())) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Аккаунт деактивирован");
                    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Вход выполнен успешно");
                response.put("userId", user.getUserId());
                response.put("email", user.getEmailAddress());
                response.put("fullName", user.getFullUserName());
                response.put("role", user.getUserRole().name());
                response.put("phone", user.getPhoneNumber());

                log.info("Успешный вход: {}", email);
                return ResponseEntity.ok(response);

            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Неверный email или пароль");
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            log.error("Ошибка входа: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Неверный email или пароль");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {

        Map<String, Object> response = new HashMap<>();

        try {
            userService.getUserByEmail(email);
            response.put("available", false);
            response.put("message", "Email уже используется");
        } catch (Exception e) {
            response.put("available", true);
            response.put("message", "Email доступен");
        }

        return ResponseEntity.ok(response);
    }
}
