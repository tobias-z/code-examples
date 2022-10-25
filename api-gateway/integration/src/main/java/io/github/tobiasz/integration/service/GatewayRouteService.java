package io.github.tobiasz.integration.service;

import io.github.tobiasz.integration.config.security.util.RouteRequestMatcher;
import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayRouteService {

    Flux<GatewayRouteDto> getAllGatewayRoutes();

    Mono<CreatedDto<String>> createGatewayRoute(GatewayRouteDto gatewayRouteDto);

    Mono<GatewayRouteDto> getMatchingRouteFromPath(RouteRequestMatcher routeRequestMatcher);
}
