package com.insurance.server.controller;

import com.insurance.server.entity.User;
import com.insurance.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя с ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        log.info("Запрос на обновление пользователя с ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String roleName = request.get("role");
        User.UserRole newRole = User.UserRole.valueOf(roleName);

        User updatedUser = userService.changeUserRole(id, newRole);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Роль успешно изменена");
        response.put("userId", updatedUser.getUserId());
        response.put("newRole", updatedUser.getUserRole().name());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Пользователь деактивирован");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Пользователь удалён");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        User.UserRole userRole = User.UserRole.valueOf(role);
        return ResponseEntity.ok(userService.getUsersByRole(userRole));
    }
}