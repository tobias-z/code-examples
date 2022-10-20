package io.github.tobiasz.integration.service;

import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.entity.GatewayRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayRouteService {

    Flux<GatewayRoute> getAllGatewayRoutes();

    Mono<CreatedDto<String>> createGatewayRoute(GatewayRouteDto gatewayRouteDto);
}
