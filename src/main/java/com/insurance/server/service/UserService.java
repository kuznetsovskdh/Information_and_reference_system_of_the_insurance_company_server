package com.insurance.server.service;

import com.insurance.server.entity.User;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;  // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ!


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // ← ИЗМЕНИТЕ НА readOnly = true (было false)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll();
    }


    public User getUserById(Long id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пользователь с ID " + id + " не найден"
                ));
    }


    public User getUserByEmail(String email) {
        log.info("=== ПОИСК ПОЛЬЗОВАТЕЛЯ ПО EMAIL ===");
        log.info("Email: {}", email);

        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info("=== НАЙДЕН ПОЛЬЗОВАТЕЛЬ ===");
            log.info("ID: {}", user.getUserId());
            log.info("Email: {}", user.getEmailAddress());
            log.info("Имя: {}", user.getFullUserName());
            log.info("Роль: {}", user.getUserRole());
            log.info("Пароль из БД: '{}'", user.getUserPassword());
            log.info("Длина пароля: {}",
                    user.getUserPassword() != null ? user.getUserPassword().length() : "null");

            return user;
        } else {
            log.error("Пользователь с email {} не найден", email);
            throw new ResourceNotFoundException(
                    "Пользователь с email " + email + " не найден"
            );
        }
    }


    @Transactional  // ← ДОБАВЬТЕ ЭТОТ АННОТАЦИЮ!
    public User createUser(User user) {
        log.info("Регистрация нового пользователя: {}", user.getEmailAddress());

        if (userRepository.existsByEmailAddress(user.getEmailAddress())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Шифрование пароля
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        user.setAccountActive(true);

        User saved = userRepository.save(user);
        log.info("Пользователь зарегистрирован с ID: {}", saved.getUserId());
        return saved;
    }


    @Transactional
    public User updateUser(Long id, User updatedUser) {
        log.info("Обновление пользователя с ID: {}", id);

        User user = getUserById(id);

        if (updatedUser.getFullUserName() != null) {
            user.setFullUserName(updatedUser.getFullUserName());
        }
        if (updatedUser.getPhoneNumber() != null) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        User saved = userRepository.save(user);
        log.info("Пользователь успешно обновлён");
        return saved;
    }


    @Transactional
    public User changeUserRole(Long id, User.UserRole newRole) {
        log.info("Изменение роли пользователя {} на {}", id, newRole);

        User user = getUserById(id);
        user.setUserRole(newRole);

        return userRepository.save(user);
    }


    @Transactional
    public void deactivateUser(Long id) {
        log.info("Деактивация пользователя с ID: {}", id);

        User user = getUserById(id);
        user.setAccountActive(false);
        userRepository.save(user);
    }


    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь с ID " + id + " не найден");
        }
        userRepository.deleteById(id);
    }


    public List<User> getUsersByRole(User.UserRole role) {
        log.info("Получение пользователей с ролью: {}", role);
        return userRepository.findByUserRole(role);
    }


    public List<User> searchUsersByName(String name) {
        log.info("Поиск пользователей по имени: {}", name);
        return userRepository.findByFullUserNameContainingIgnoreCase(name);
    }
}