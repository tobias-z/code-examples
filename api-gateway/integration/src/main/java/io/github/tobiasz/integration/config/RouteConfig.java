package io.github.tobiasz.integration.config;

import static io.github.tobiasz.integration.config.RouteConstants.CATCH_ALL_FALLBACK;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Configuration
@RequiredArgsConstructor
public class RouteConfig implements RouteLocator {

    private final GatewayRouteService gatewayRouteService;
    private final RouteLocatorBuilder routeLocatorBuilder;

    @Override
    public Flux<Route> getRoutes() {
        return gatewayRouteService.getAllGatewayRoutes()
            .zipWith(Mono.just(routeLocatorBuilder.routes()))
            .map(this::buildRoute)
            .flatMap(builder -> builder.build().getRoutes());
    }

    private Builder buildRoute(Tuple2<GatewayRouteDto, Builder> objects) {
        Builder builder = objects.getT2();
        GatewayRouteDto gatewayRoute = objects.getT1();
        // TODO: add stuff depending on the stuff in the database
        builder.route(gatewayRoute.getId(), predicateSpec -> {
            String requestPath = gatewayRoute.getRequestPath();
            return predicateSpec
                .path(requestPath)
                .filters(f -> f
                    // to use this circuitBreaker functionality the dependency 'spring-cloud-starter-circuitbreaker-reactor-resilience4j' is required
                    .circuitBreaker(config -> config.setFallbackUri("forward:" + CATCH_ALL_FALLBACK))
                    .rewritePath("/%s/(?<segment>.*)".formatted(requestPath), "/${segment}"))
                .uri(gatewayRoute.getForwardUri());
        });
        return builder;
    }
}
