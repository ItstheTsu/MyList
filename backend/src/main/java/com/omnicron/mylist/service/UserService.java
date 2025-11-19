package com.omnicron.mylist.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.repository.UserRepository;
import com.omnicron.mylist.security.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }
        return user;
    }

    // Criar usuário (registro)
    // UserService
    public User createUser(User user) {
        // Evita duplicação
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            throw new RuntimeException("Usuário já existe");
        }

        // Valores default
        if (user.getSalary() == null)
            user.setSalary(0f);
        if (user.getLimitValue() == null)
            user.setLimitValue(0f);
        if (user.getCurrency() == null)
            user.setCurrency("R$");

        // Criptografa a senha
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Salva e retorna
        return userRepository.save(user);
    }

    // Login corrigido (com BCrypt)
    public User login(String email, String password) {
        System.out.println("LOGIN TENTADO: " + email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            System.out.println("Usuário não encontrado");
            return null;
        }

        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("Comparando senha...");

        // VERIFICA SENHA CRIPTOGRAFADA
        if (passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Senha correta!");
            return user;
        }

        System.out.println("Senha incorreta!");
        return null;
    }

    // Buscar todos
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Buscar por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Atualizar configurações do usuário
    public void updateUserConfig(Long id, User config) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setSalary(config.getSalary());
        user.setLimitValue(config.getLimitValue());
        user.setCurrency(config.getCurrency());

        userRepository.save(user);
    }
}
