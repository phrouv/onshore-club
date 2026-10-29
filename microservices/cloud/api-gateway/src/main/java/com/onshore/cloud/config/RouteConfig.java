package com.onshore.cloud.config;

import com.onshore.cloud.service.TokenBlacklistService;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

  private static final List<String> DOC_SERVICES =
      List.of(
          "user-service",
          "booking-service",
          "payment-service",
          "partner-service",
          "planning-service",
          "credit-service",
          "payout-service",
          "admin-service");

  private final JwtUtil jwtUtil;
  private final TokenBlacklistService blacklistService;

  public RouteConfig(JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
    this.jwtUtil = jwtUtil;
    this.blacklistService = blacklistService;
  }

  // ==================== Public routes (no JWT) ====================

  @Bean
  public RouterFunction<ServerResponse> authRoutes() {
    return GatewayRouterFunctions.route("auth-routes")
        .route(RequestPredicates.path("/auth/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("user-service"))
        // .filter(CircuitBreakerFilterFunctions.circuitBreaker("user-service-cb",
        // URI.create("forward:/fallback")))
        .build();
  }

  // Public: proxies /docs/{service}/v3/api-docs to each service's OpenAPI spec
  // for the Swagger UI.
  @Bean
  public RouterFunction<ServerResponse> apiDocsRoutes() {
    return DOC_SERVICES.stream()
        .map(
            svc ->
                GatewayRouterFunctions.route(svc + "-docs")
                    .route(
                        RequestPredicates.path("/docs/" + svc + "/v3/api-docs/**"),
                        HandlerFunctions.http())
                    .filter(LoadBalancerFilterFunctions.lb(svc))
                    .before(
                        BeforeFilterFunctions.rewritePath(
                            "/docs/" + svc + "/(?<segment>.*)", "/${segment}"))
                    .build())
        .reduce(RouterFunction::and)
        .orElseThrow();
  }

  // ==================== Protected routes (JWT required) ====================

  @Bean
  public RouterFunction<ServerResponse> userServiceRoutes() {
    return GatewayRouterFunctions.route("user-service-routes")
        .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("user-service"))
        // .filter(CircuitBreakerFilterFunctions.circuitBreaker("user-service-cb",
        // URI.create("forward:/fallback")))
        .before(this::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> bookingServiceRoutes() {
    return GatewayRouterFunctions.route("booking-service-routes")
        .route(RequestPredicates.path("/api/bookings/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("booking-service"))
        // .filter(CircuitBreakerFilterFunctions.circuitBreaker("booking-service-cb",
        // URI.create("forward:/fallback")))
        .before(this::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> paymentServiceRoutes() {
    return GatewayRouterFunctions.route("payment-service-routes")
        .route(RequestPredicates.path("/api/payments/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("payment-service"))
        // .filter(CircuitBreakerFilterFunctions.circuitBreaker("payment-service-cb",
        // URI.create("forward:/fallback")))
        .before(this::jwtAuthFilter)
        .build();
  }

  // ==================== JWT filter ====================

  ServerRequest jwtAuthFilter(ServerRequest request) {
    String authHeader = request.headers().firstHeader(JwtConstant.JWT_HEADER);

    if (authHeader == null || !authHeader.startsWith(JwtConstant.TOKEN_PREFIX)) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
    }

    String token = authHeader.substring(JwtConstant.TOKEN_PREFIX.length());

    if (!jwtUtil.isTokenValid(token)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
    }

    if (blacklistService.isBlacklisted(token)) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Token has been revoked. Please log in again.");
    }

    String email = jwtUtil.extractEmail(token);
    String authorities = jwtUtil.extractAuthorities(token);
    UUID userId = jwtUtil.extractUserId(token);

    return ServerRequest.from(request)
        .header("X-User-Id", String.valueOf(userId))
        .header("X-User-Email", email)
        .header("X-User-Roles", authorities)
        .build();
  }

  ServerRequest requireRole(ServerRequest request, String role) {
    String roles = request.headers().firstHeader("X-User-Roles");
    if (roles == null || !roles.contains(role)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Access denied. Required role: " + role);
    }
    return request;
  }
}
