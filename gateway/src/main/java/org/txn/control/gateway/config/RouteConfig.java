package org.txn.control.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.txn.control.gateway.filters.JwtAuthFilter;

@Configuration
public class RouteConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("protected_route", r -> r.path("/protected/**")
                        .filters(f -> f.filter(jwtAuthFilter::filter))
                        .uri("lb://PROTECTED-SERVICE"))
                .build();
    }
}
