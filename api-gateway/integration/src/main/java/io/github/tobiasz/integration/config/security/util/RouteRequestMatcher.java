package io.github.tobiasz.integration.config.security.util;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Setter
public class RouteRequestMatcher {

    private ServerWebExchange exchange;

    public Mono<Boolean> matches(String matchAgainst) {
        return ServerWebExchangeMatchers.pathMatchers(matchAgainst)
            .matches(exchange)
            .map(MatchResult::isMatch);
    }

}
