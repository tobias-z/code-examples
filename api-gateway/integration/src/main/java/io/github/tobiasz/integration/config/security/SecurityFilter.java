package io.github.tobiasz.integration.config.security;

import static io.github.tobiasz.integration.config.Constants.USER_ID_HEADER;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.errorhandling.UnauthorizedException;
import io.github.tobiasz.integration.service.AuthMethodService;
import io.github.tobiasz.integration.service.GatewayRouteService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends WebFilterChainProxy {

    private final List<AuthMethodService> authMethodServices;
    private final GatewayRouteService gatewayRouteService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Make sure we don't have some cheeky hackers using a random user id
        HttpHeaders headers = exchange.getRequest()
            .mutate()
            .headers(httpHeaders -> httpHeaders.remove(USER_ID_HEADER))
            .build()
            .getHeaders();

        return getAuthHeader(headers)
            .zipWith(gatewayRouteService.getMatchingRouteFromPath(getRouteRequestMatcher(exchange)))
            .map(this::toAuthDto)
            .flatMap(authDto -> {
                boolean shouldAuthenticate = authDto.authMethodService.shouldAuthenticate(authDto.gatewayRouteDto());
                if (!shouldAuthenticate) {
                    return chain.filter(exchange);
                }

                Optional<Integer> userId = authDto.authMethodService.authenticate(authDto.token());

                if (userId.isEmpty()) {
                    return Mono.empty();
                }

                ServerWebExchange exchangeWithUserId = exchange.mutate()
                    .request(builder -> builder.header(USER_ID_HEADER, String.valueOf(userId.orElseThrow()))).build();
                return chain.filter(exchangeWithUserId);
            })
            .switchIfEmpty(Mono.error(new UnauthorizedException()));
    }

    private static Mono<String[]> getAuthHeader(HttpHeaders headers) {
        return Mono.justOrEmpty(headers.get("Authorization"))
            .map(strings -> strings.get(0))
            .map(authHeader -> authHeader.split(" "))
            // An authorization header should always have a method such as Bearer or Basic
            .filter(strings -> strings.length > 1);
    }

    private static RouteRequestMatcher getRouteRequestMatcher(ServerWebExchange exchange) {
        return matchAgainst -> ServerWebExchangeMatchers.pathMatchers(matchAgainst)
            .matches(exchange)
            .map(matchResult -> {
                System.out.println(matchResult);
                return matchResult.isMatch();
            });
    }

    private AuthDto toAuthDto(Tuple2<String[], GatewayRouteDto> tuple2) {
        String[] authHeader = tuple2.getT1();
        return authMethodServices
            .stream()
            .filter(authMethodService -> authMethodService.canHandleMethod(authHeader[0]))
            .findFirst()
            .map(authMethodService -> new AuthDto(authHeader[1], authMethodService, tuple2.getT2()))
            .orElse(null);
    }

    record AuthDto(String token, AuthMethodService authMethodService, GatewayRouteDto gatewayRouteDto) {

    }
}
