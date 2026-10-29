package com.onshore.cloud.config;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Routes of the sports-club services. Every route requires a valid JWT; role checks beyond
 * admin-only areas are enforced by the services from the X-User-Roles header. The /internal/**
 * endpoints used between services are intentionally not routed.
 */
@Configuration
public class ClubRouteConfig {

  private final RouteConfig auth;

  public ClubRouteConfig(RouteConfig auth) {
    this.auth = auth;
  }

  @Bean
  public RouterFunction<ServerResponse> partnerServiceRoutes() {
    return GatewayRouterFunctions.route("partner-service-routes")
        .route(RequestPredicates.path("/api/partners/**"), HandlerFunctions.http())
        .route(RequestPredicates.path("/api/activities/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("partner-service"))
        .before(auth::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> planningServiceRoutes() {
    return GatewayRouterFunctions.route("planning-service-routes")
        .route(RequestPredicates.path("/api/classes/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("planning-service"))
        .before(auth::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> creditServiceRoutes() {
    return GatewayRouterFunctions.route("credit-service-routes")
        .route(RequestPredicates.path("/api/credits/**"), HandlerFunctions.http())
        .route(RequestPredicates.path("/api/plans/**"), HandlerFunctions.http())
        .route(RequestPredicates.path("/api/subscriptions/**"), HandlerFunctions.http())
        .route(RequestPredicates.path("/api/credit-settings/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("credit-service"))
        .before(auth::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> payoutServiceRoutes() {
    return GatewayRouterFunctions.route("payout-service-routes")
        .route(RequestPredicates.path("/api/payouts/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("payout-service"))
        .before(auth::jwtAuthFilter)
        .build();
  }

  @Bean
  public RouterFunction<ServerResponse> adminServiceRoutes() {
    return GatewayRouterFunctions.route("admin-service-routes")
        .route(RequestPredicates.path("/api/admin/**"), HandlerFunctions.http())
        .filter(LoadBalancerFilterFunctions.lb("admin-service"))
        .before(auth::jwtAuthFilter)
        .before(request -> auth.requireRole(request, "ROLE_SYSTEM_ADMIN"))
        .build();
  }
}
