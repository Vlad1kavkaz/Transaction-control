package org.txn.control.gateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter {

    @Value("${jwt.secret}")
    private String secret;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return Mono.error(new RuntimeException("Missing Authorization Header"));
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Invalid Authorization Header"));
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            exchange.getRequest().mutate()
                    .header("X-User-Role", claims.get("role", String.class))
                    .header("X-User-Name", claims.getSubject())
                    .build();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Invalid Token"));
        }

        return chain.filter(exchange);
    }
}