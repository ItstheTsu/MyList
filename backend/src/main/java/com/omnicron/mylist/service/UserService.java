package com.omnicron.mylist.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omnicron.mylist.model.User;
import com.omnicron.mylist.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }
        return user;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User login(String email, String password) {
        System.out.println("LOGIN TENTADO: " + email + " / " + password);
        User user = userRepository.findByEmail(email);
        System.out.println("ENCONTRADO: " + (user != null ? user.getEmail() : "NADA"));
        if (user != null && user.getPassword().trim().equals(password.trim())) {
            System.out.println("SENHA NO BANCO: " + user.getPassword());
            System.out.println("SENHA DIGITADA: " + password);
            return user;
        }
        return null;
    }

    public User createUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            throw new RuntimeException("Usuário já existe");
        }

        if (user.getSalary() == null)
            user.setSalary(0f);
        if (user.getLimitValue() == null)
            user.setLimitValue(0f);
        if (user.getCurrency() == null)
            user.setCurrency("R$");

        return userRepository.save(user);
    }

    public void updateUserConfig(Long id, User config) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setSalary(config.getSalary());
        user.setLimitValue(config.getLimitValue());
        user.setCurrency(config.getCurrency());

        userRepository.save(user);
    }
}