package io.github.tobiasz.integration.config.security;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.errorhandling.UnauthorizedException;
import io.github.tobiasz.integration.service.AuthMethodService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends WebFilterChainProxy {

    private final List<AuthMethodService> authMethodServices;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // TODO: could do something with the path so that each service can use a different form of authentication
        ServerHttpRequest request = exchange.getRequest();
        return Mono.justOrEmpty(request.getHeaders().get("Authorization"))
            .log()
            .map(strings -> strings.get(0))
            .map(authHeader -> authHeader.split(" "))
            // An authorization header should always have a method such as Bearer or Basic
            .filter(strings -> strings.length > 1)
            .map(this::toAuthMethod)
            .filter(method -> method.authMethodService.shouldAuthenticate(new GatewayRouteDto()))
            .flatMap(method -> method.authMethodService.authenticate(method.token()))
            .map(id -> {
                // TODO: IMPORTANT CHECK IF THIS HEADER ALREADY EXISTS. IF IT DOES DENY THE REQUEST
                return exchange.mutate().request(builder -> builder.header("user_id", String.valueOf(id))).build();
            })
            .flatMap(chain::filter)
            .switchIfEmpty(Mono.error(new UnauthorizedException()));
    }

    private AuthMethod toAuthMethod(String[] strings) {
        return this.authMethodServices
            .stream()
            .filter(authMethodService -> authMethodService.canHandleMethod(strings[0]))
            .findFirst()
            .map(authMethodService -> new AuthMethod(strings[1], authMethodService))
            .orElse(null);
    }

    record AuthMethod(String token, AuthMethodService authMethodService) {
    }
}
