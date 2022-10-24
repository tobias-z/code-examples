package io.github.tobiasz.integration.service.impl;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.service.AuthMethodService;
import java.util.Optional;
import org.springframework.stereotype.Service;

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
    public Optional<Integer> authenticate(String token) {
        // TODO: Actually do something
        return Optional.of(1);
    }

}
