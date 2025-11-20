package com.omnicron.mylist.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omnicron.mylist.dto.LoginRequest;
import com.omnicron.mylist.dto.LoginResponse;
import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.security.JwtUtil;
import com.omnicron.mylist.service.EmailService;
import com.omnicron.mylist.service.PasswordResetService;
import com.omnicron.mylist.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtUtil jwtUtil,
            PasswordResetService passwordResetService,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            System.out.println("Autenticação bem-sucedida para: " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        }

        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        user.getId(),
                        user.getName(),
                        user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User saved = userService.createUser(user);
            String token = jwtUtil.generateToken(saved.getEmail());

            // devolve usuário + token
            return ResponseEntity.ok(Map.of(
                    "user", saved,
                    "token", token));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Recuperar senha - gerar token e enviar email

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        String token = passwordResetService.generateToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/recover-password/{token}")
    public ResponseEntity<?> getEmailByToken(@PathVariable String token) {
        String email = passwordResetService.getEmailByToken(token);
        if (email == null)
            return ResponseEntity.status(404).body("Token inválido ou expirado");
        return ResponseEntity.ok(Map.of("email", email));
    }

    @PostMapping("/recover-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token,
            @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        boolean success = passwordResetService.updatePassword(token, newPassword);
        if (!success)
            return ResponseEntity.status(400).body("Token inválido ou usuário não encontrado");
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }
}