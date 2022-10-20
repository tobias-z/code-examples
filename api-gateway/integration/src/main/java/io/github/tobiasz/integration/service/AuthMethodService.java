package io.github.tobiasz.integration.service;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import java.util.Optional;
import reactor.core.publisher.Mono;

public interface AuthMethodService {

    boolean canHandleMethod(String method);

    Mono<Integer> authenticate(String token);

    boolean shouldAuthenticate(GatewayRouteDto gatewayRouteDto);
}
