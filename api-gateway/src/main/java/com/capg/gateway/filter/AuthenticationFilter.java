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

/**
 * Gateway Authentication Filter
 * Handles JWT token verification for incoming requests
 *
 * Exception Handling:
 * - Missing/Invalid Token: Returns HTTP 401 Unauthorized via unAuthorized()
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey123";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Filters incoming HTTP requests and validates Authorization headers
     *
     * @param exchange ServerWebExchange
     * @param chain GatewayFilterChain
     * @return Mono<Void> indicating the completion of request processing
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String path = request.getURI().getPath();

        // Ensure requests hitting Eureka, Swagger, or specific authentication endpoints bypass checking
        if (path.contains("/eureka") || path.contains("/api/auth/register") || path.contains("/api/auth/login")
                || path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/swagger-resources") || path.contains("/webjars")) {
            return chain.filter(exchange);
        }

        // Bypass complete checking for CORS preflight OPTIONS requests
        if (request.getMethod() != null && request.getMethod().matches("OPTIONS")) {
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

    /**
     * Helper method to return HTTP 401 Unauthorized Response
     *
     * @param response ServerHttpResponse
     * @param message Error message
     * @return Mono<Void> indicating the completion of response formulation
     */
    private Mono<Void> unAuthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
