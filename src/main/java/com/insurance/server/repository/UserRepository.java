package com.insurance.server.repository;

import com.insurance.server.entity.User;
import com.insurance.server.entity.User.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);


    List<User> findByUserRole(UserRole userRole);


    List<User> findByFullUserNameContainingIgnoreCase(String fullUserName);
}