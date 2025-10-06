package com.nisircop.le.apigateway.filter;

import com.nisircop.le.apigateway.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return unauthorized(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    io.jsonwebtoken.Claims claims = jwtUtil.getAllClaimsFromToken(token);
                    if (claims.getExpiration().before(new java.util.Date())) {
                        return unauthorized(exchange);
                    }
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Id", String.valueOf(claims.get("userId")))
                                    .header("X-User-Role", String.valueOf(claims.get("role")))
                                    .build())
                            .build();
                    return chain.filter(mutatedExchange);
                } catch (Exception e) {
                    return unauthorized(exchange);
                }
            } else {
                return unauthorized(exchange);
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Put the configuration properties here
    }
}