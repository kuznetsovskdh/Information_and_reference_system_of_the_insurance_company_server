package com.insurance.server.repository;

import com.insurance.server.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    List<Client> findByFullClientNameContainingIgnoreCase(String name);


    List<Client> findByPassportNumber(String passportNumber);


    List<Client> findByEmailAddressContainingIgnoreCase(String email);


    List<Client> findByManagerUser_UserId(Long managerId);


    List<Client> findByPhoneNumberContaining(String phone);


    @Query("SELECT COUNT(c) FROM Client c")
    long getTotalClientsCount();
}
