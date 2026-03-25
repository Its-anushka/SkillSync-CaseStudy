package com.capg.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey123";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String path = request.getURI().getPath();

        // Ensure requests hitting Eureka or specific authentication endpoints bypass checking
        if (path.contains("/eureka") || path.contains("/api/auth/register") || path.contains("/api/auth/login")) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey("Authorization")) {
            return unAuthorized(exchange.getResponse(), "Missing Authorization Header");
        }

        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return unAuthorized(exchange.getResponse(), "Invalid Authorization Header Format");
        }

        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            // In a more robust system, you might extract user roles from token here and inject them as headers 
        } catch (Exception e) {
            return unAuthorized(exchange.getResponse(), "Invalid or Expired JWT Token");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> unAuthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
