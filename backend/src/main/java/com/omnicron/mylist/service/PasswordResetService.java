package com.omnicron.mylist.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.repository.UserRepository;

@Service
public class PasswordResetService {

    private final Map<String, TokenData> tokens = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenData(user.getEmail(), System.currentTimeMillis()));
        return token;
    }

    public String getEmailByToken(String token) {
        TokenData data = tokens.get(token);
        if (data == null)
            return null;

        // Expira em 30 minutos
        if (System.currentTimeMillis() - data.timestamp > 30 * 60 * 1000) {
            tokens.remove(token);
            return null;
        }
        return data.email;
    }

    public boolean updatePassword(String token, String newPassword) {
        String email = getEmailByToken(token);
        if (email == null)
            return false;

        User user = userRepository.findByEmail(email);
        if (user == null)
            return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokens.remove(token);
        return true;
    }

    private static class TokenData {
        String email;
        long timestamp;

        TokenData(String email, long timestamp) {
            this.email = email;
            this.timestamp = timestamp;
        }
    }
}