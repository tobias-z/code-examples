package io.github.tobiasz.integration.service.impl;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.service.AuthMethodService;
import java.util.Optional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtAuthService implements AuthMethodService {

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equals("Bearer");
    }

    @Override
    public boolean shouldAuthenticate(GatewayRouteDto gatewayRouteDto) {
        // TODO: should use variable from db
        return true;
    }

    @Override
    public Mono<Integer> authenticate(String token) {
        // TODO: Actually do something
        return Mono.just(1);
    }

}
