package com.omnicron.mylist.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.security.JwtUtil;
import com.omnicron.mylist.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/me")
    public User getLoggedUser(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User saved = userService.createUser(user);

            String token = jwtUtil.generateToken(saved.getEmail());

            Map<String, Object> res = new HashMap<>();
            res.put("user", saved);
            res.put("token", token);

            return ResponseEntity.status(HttpStatus.CREATED).body(res);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já cadastrado");

        }
    }

    @PutMapping("/{id}/config")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody User config) {
        try {
            userService.updateUserConfig(id, config);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
