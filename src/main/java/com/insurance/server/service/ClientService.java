package com.insurance.server.service;

import com.insurance.server.entity.Client;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;


    public List<Client> getAllClients() {
        log.info("Получение списка всех клиентов");
        return clientRepository.findAll();
    }


    public Client getClientById(Long id) {
        log.info("Поиск клиента с ID: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Клиент с ID " + id + " не найден"
                ));
    }


    @Transactional
    public Client createClient(Client client) {
        log.info("Регистрация нового клиента: {}", client.getFullClientName());
        Client saved = clientRepository.save(client);
        log.info("Клиент зарегистрирован с ID: {}", saved.getClientId());
        return saved;
    }


    @Transactional
    public Client updateClient(Long id, Client updatedClient) {
        log.info("Обновление клиента с ID: {}", id);

        Client client = getClientById(id);

        client.setFullClientName(updatedClient.getFullClientName());
        client.setBirthDate(updatedClient.getBirthDate());
        client.setPassportSeries(updatedClient.getPassportSeries());
        client.setPassportNumber(updatedClient.getPassportNumber());
        client.setClientAddress(updatedClient.getClientAddress());
        client.setPhoneNumber(updatedClient.getPhoneNumber());
        client.setEmailAddress(updatedClient.getEmailAddress());
        client.setInnNumber(updatedClient.getInnNumber());

        if (updatedClient.getManagerUser() != null) {
            client.setManagerUser(updatedClient.getManagerUser());
        }

        Client saved = clientRepository.save(client);
        log.info("Клиент успешно обновлён");
        return saved;
    }


    @Transactional
    public void deleteClient(Long id) {
        log.info("Удаление клиента с ID: {}", id);

        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Клиент с ID " + id + " не найден");
        }
        clientRepository.deleteById(id);
    }


    public List<Client> searchClientsByName(String name) {
        log.info("Поиск клиентов по имени: {}", name);
        return clientRepository.findByFullClientNameContainingIgnoreCase(name);
    }


    public List<Client> getClientsByManager(Long managerId) {
        log.info("Получение клиентов менеджера с ID: {}", managerId);
        return clientRepository.findByManagerUser_UserId(managerId);
    }


    public List<Client> searchByPhone(String phone) {
        log.info("Поиск клиентов по телефону: {}", phone);
        return clientRepository.findByPhoneNumberContaining(phone);
    }


    public long getTotalClientsCount() {
        return clientRepository.getTotalClientsCount();
    }
}
