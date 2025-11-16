package com.omnicron.mylist.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omnicron.mylist.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Buscar usuário por email
    Optional<User> findByEmail(String email);
}