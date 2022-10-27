package io.github.tobiasz.integration.config.security.authmethod;

import io.github.tobiasz.integration.config.security.util.AuthStatus;
import io.github.tobiasz.integration.config.security.util.RouteRequestMatcher;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthMethod {

    boolean canHandleMethod(String method);

    Optional<Integer> authenticate(String token);

    default Mono<AuthStatus> shouldAuthenticate(RouteRequestMatcher routeRequestMatcher, GatewayRouteDto gatewayRouteDto) {
        List<Mono<AuthStatus>> results = new ArrayList<>();

        List<RemoveAfter> removeAfters = List.of(
            new RemoveAfter("/bob/**", "Basic"),
            new RemoveAfter("/gateway/**", "Bearer")
        );

        for (RemoveAfter removeAfter : removeAfters) {
            Mono<AuthStatus> authStatusMono = routeRequestMatcher
                .matches(removeAfter.path())
                .zipWith(Mono.just(removeAfter))
                .map(objects -> {
                    boolean matched = objects.getT1();
                    RemoveAfter remove = objects.getT2();
                    if (!matched) {
                        return AuthStatus.NO_MATCH;
                    }

                    if (remove.method().equalsIgnoreCase("NONE")) {
                        return AuthStatus.AUTHORIZED;
                    }

                    if (!canHandleMethod(remove.method())) {
                        return AuthStatus.INVALID_METHOD;
                    }

                    return AuthStatus.NEEDS_AUTHENTICATION;
                })
                .filter(authStatus -> !authStatus.equals(AuthStatus.NO_MATCH));

            results.add(authStatusMono);
        }

        return Flux.fromIterable(results)
            .flatMap(booleanMono -> booleanMono)
            .next();
    }

    record RemoveAfter(String path, String method) {

    }
}
