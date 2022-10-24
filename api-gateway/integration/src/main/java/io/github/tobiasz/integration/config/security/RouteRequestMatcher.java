package io.github.tobiasz.integration.config.security;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface RouteRequestMatcher {

    Mono<Boolean> matches(String matchAgainst);

}
