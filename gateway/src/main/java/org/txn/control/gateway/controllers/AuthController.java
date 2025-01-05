package org.txn.control.gateway.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Key;
import java.util.Map;

@RestController
@RequestMapping("/person-reg")
public class AuthController {

    @Value("${jwt.secret}")
    private String secret;

    private final WebClient webClient;

    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service").build();
    }

    @PostMapping("/exist-user")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        // Шлём запрос на сервис пользователей
        String role = webClient.post()
                .uri("/exist-user")
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (role == null || role.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Создаём JWT
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .setSubject(credentials.get("username"))
                .claim("role", role)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return ResponseEntity.ok(Map.of("token", token));
    }
}
