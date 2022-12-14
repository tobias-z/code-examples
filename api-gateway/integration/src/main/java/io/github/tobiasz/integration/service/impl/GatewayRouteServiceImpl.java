package io.github.tobiasz.integration.service.impl;

import io.github.tobiasz.integration.config.security.util.RouteRequestMatcher;
import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.entity.GatewayRoute;
import io.github.tobiasz.integration.repository.GatewayRouteRepository;
import io.github.tobiasz.integration.service.GatewayRouteService;
import io.github.tobiasz.integration.service.RouteService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class GatewayRouteServiceImpl implements GatewayRouteService {

    private final GatewayRouteRepository gatewayRouteRepository;
    private List<GatewayRouteDto> gatewayRouteDtoList;
    private final RouteService routeService;

    @Override
    public Flux<GatewayRouteDto> getAllGatewayRoutes() {
        gatewayRouteDtoList = new ArrayList<>();
        System.out.println("calling get all");
        return gatewayRouteRepository.findAll()
            .map(gatewayRoute -> {
                System.out.println("printing: " + gatewayRoute.getRequestPath());
                return gatewayRoute;
            })
            .map(GatewayRouteDto::fromEntity)
            .doOnNext(gatewayRouteDtoList::add);
    }

    @Override
    public Mono<CreatedDto<String>> createGatewayRoute(GatewayRouteDto gatewayRouteDto) {
        return Mono.just(gatewayRouteDto)
            .map(GatewayRoute::fromDto)
            .flatMap(gatewayRouteRepository::save)
            .map(GatewayRoute::getId)
            .map(CreatedDto::toCreated);
    }

    @Override
    public Mono<GatewayRouteDto> getMatchingRouteFromPath(RouteRequestMatcher routeRequestMatcher) {
        return Flux.fromIterable(gatewayRouteDtoList)
            .zipWith(Mono.just(routeRequestMatcher))
            .flatMap(this::matches)
            .singleOrEmpty();
    }

    private Mono<GatewayRouteDto> matches(Tuple2<GatewayRouteDto, RouteRequestMatcher> tuple2) {
        return Mono.just(tuple2)
            .filterWhen(o -> {
                GatewayRouteDto routeDto = o.getT1();
                RouteRequestMatcher matcher = o.getT2();
                return matcher.matches(routeDto.getRequestPath());
            })
            .map(Tuple2::getT1);
    }

}
