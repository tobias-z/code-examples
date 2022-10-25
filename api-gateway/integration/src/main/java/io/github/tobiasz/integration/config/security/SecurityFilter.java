package io.github.tobiasz.integration.config.security;

import io.github.tobiasz.integration.config.properties.SecurityProperties;
import io.github.tobiasz.integration.config.security.authmethod.AuthMethod;
import io.github.tobiasz.integration.config.security.util.AuthStatus;
import io.github.tobiasz.integration.config.security.util.RouteRequestMatcher;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.errorhandling.UnauthorizedException;
import io.github.tobiasz.integration.service.GatewayRouteService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends WebFilterChainProxy {

    private final List<AuthMethod> authMethods;
    private final GatewayRouteService gatewayRouteService;
    private final SecurityProperties securityProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Make sure we don't have some cheeky hackers using a random user id
        HttpHeaders headers = exchange.getRequest()
            .mutate()
            .headers(httpHeaders -> httpHeaders.remove(securityProperties.getUserIdHeaderName()))
            .build()
            .getHeaders();

        RouteRequestMatcher routeRequestMatcher = new RouteRequestMatcher(exchange);
        return getAuthHeader(headers)
            .zipWith(gatewayRouteService.getMatchingRouteFromPath(routeRequestMatcher))
            .map(this::toAuthDto)
            .flatMap(dto -> dto.authMethod.shouldAuthenticate(routeRequestMatcher, dto.gatewayRouteDto())
                .zipWith(Mono.just(dto))
                .flatMap(tuple -> {
                    AuthStatus authStatus = tuple.getT1();
                    if (authStatus.equals(AuthStatus.AUTHORIZED)) {
                        return chain.filter(exchange);
                    }

                    if (authStatus.equals(AuthStatus.INVALID_METHOD)) {
                        return Mono.error(new UnauthorizedException());
                    }

                    AuthDto authDto = tuple.getT2();
                    Optional<Integer> userId = authDto.authMethod.authenticate(authDto.token());

                    if (userId.isEmpty()) {
                        return Mono.error(new UnauthorizedException());
                    }

                    ServerWebExchange exchangeWithUserId = exchange.mutate().request(builder -> builder.header(
                        securityProperties.getUserIdHeaderName(),
                        String.valueOf(userId.orElseThrow()))
                    ).build();
                    return chain.filter(exchangeWithUserId);
                })
            );
    }

    private static Mono<String[]> getAuthHeader(HttpHeaders headers) {
        return Mono.justOrEmpty(headers.get("Authorization"))
            .map(strings -> strings.get(0))
            .map(authHeader -> authHeader.split(" "))
            // An authorization header should always have a method such as Bearer or Basic
            .filter(strings -> strings.length > 1);
    }

    private AuthDto toAuthDto(Tuple2<String[], GatewayRouteDto> tuple2) {
        String[] authHeader = tuple2.getT1();
        return authMethods
            .stream()
            .filter(authMethod -> authMethod.canHandleMethod(authHeader[0]))
            .findFirst()
            .map(authMethod -> new AuthDto(authHeader[1], authMethod, tuple2.getT2()))
            .orElse(null);
    }

    record AuthDto(String token, AuthMethod authMethod, GatewayRouteDto gatewayRouteDto) {

    }
}
