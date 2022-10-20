package io.github.tobiasz.integration.service.impl;

import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.entity.GatewayRoute;
import io.github.tobiasz.integration.repository.GatewayRouteRepository;
import io.github.tobiasz.integration.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GatewayRouteServiceImpl implements GatewayRouteService {

    private final GatewayRouteRepository gatewayRouteRepository;

    @Override
    public Flux<GatewayRoute> getAllGatewayRoutes() {
        return gatewayRouteRepository.findAll();
    }

    public Mono<CreatedDto<String>> createGatewayRoute(GatewayRouteDto gatewayRouteDto) {
        return Mono.just(gatewayRouteDto)
            .map(GatewayRoute::fromDto)
            .flatMap(gatewayRouteRepository::save)
            .map(GatewayRoute::getId)
            .map(CreatedDto::toCreated);
    }


}
