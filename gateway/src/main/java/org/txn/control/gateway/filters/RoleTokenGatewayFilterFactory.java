package org.txn.control.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.txn.control.gateway.services.JwtService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final String X_ROLE_TOKEN_HEADER = "X-Role-Token";
    private static final List<String> ALLOWED_PATHS = List.of("/person-reg/v1/api/exist-user");

    private final JwtService jwtService;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            if (ALLOWED_PATHS.contains(path)) {
                log.debug("Skipping token validation for path: {}", path);
                return chain.filter(exchange);
            }

            String token = exchange.getRequest().getHeaders().getFirst(X_ROLE_TOKEN_HEADER);
            if (token == null || token.isEmpty()) {
                log.error("Missing or empty X-Role-Token header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if (!isValidToken(token, exchange)) {
                log.error("Invalid X-Role-Token: {}", token);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            log.debug("Token validated successfully for path: {}", path);
            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token, ServerWebExchange exchange) {
        if (!exchange.getRequest().getMethod().equals(HttpMethod.GET)
                && jwtService.validateToken(token, "ADMIN")) {
            return true;
        } else return exchange.getRequest().getMethod().equals(HttpMethod.GET)
                && (jwtService.validateToken(token, "USER")
                    || jwtService.validateToken(token, "ADMIN"));
    }
}
