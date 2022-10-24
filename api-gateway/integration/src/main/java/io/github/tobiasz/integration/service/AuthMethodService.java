package io.github.tobiasz.integration.service;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import java.util.Optional;

public interface AuthMethodService {

    boolean canHandleMethod(String method);

    Optional<Integer> authenticate(String token);

    boolean shouldAuthenticate(GatewayRouteDto gatewayRouteDto);
}
