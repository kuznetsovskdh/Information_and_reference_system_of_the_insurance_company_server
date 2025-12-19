package com.insurance.server.controller;

import com.insurance.server.entity.Client;
import com.insurance.server.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ClientController {

    private final ClientService clientService;


    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Client>> getAllClients() {
        log.info("Запрос списка всех клиентов");
        return ResponseEntity.ok(clientService.getAllClients());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        log.info("Запрос клиента с ID: {}", id);
        return ResponseEntity.ok(clientService.getClientById(id));
    }


    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClients(@RequestParam String name) {
        log.info("Поиск клиентов по имени: {}", name);
        return ResponseEntity.ok(clientService.searchClientsByName(name));
    }


    @GetMapping("/search-phone")
    public ResponseEntity<List<Client>> searchByPhone(@RequestParam String phone) {
        log.info("Поиск клиентов по телефону: {}", phone);
        return ResponseEntity.ok(clientService.searchByPhone(phone));
    }


    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Client>> getClientsByManager(@PathVariable Long managerId) {
        log.info("Получение клиентов менеджера с ID: {}", managerId);
        return ResponseEntity.ok(clientService.getClientsByManager(managerId));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createClient(@RequestBody Client client) {
        log.info("Запрос на создание клиента: {}", client.getFullClientName());

        Client created = clientService.createClient(client);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Клиент успешно создан");
        response.put("client", created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Client> updateClient(
            @PathVariable Long id,
            @RequestBody Client client) {
        log.info("Запрос на обновление клиента с ID: {}", id);
        return ResponseEntity.ok(clientService.updateClient(id, client));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long id) {
        log.info("Запрос на удаление клиента с ID: {}", id);

        clientService.deleteClient(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Клиент удалён");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/stats/count")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getClientsCount() {
        long count = clientService.getTotalClientsCount();

        Map<String, Object> response = new HashMap<>();
        response.put("totalClients", count);

        return ResponseEntity.ok(response);
    }
}
