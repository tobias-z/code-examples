package io.github.tobiasz.basicgateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("some_identifier", spec -> spec.path("/orders/**")
                .filters(f -> f
                    // to use this circuitBreaker functionality the dependency 'spring-cloud-starter-circuitbreaker-reactor-resilience4j' is required
                    .circuitBreaker(config -> config.setFallbackUri("forward:/catch-all-fallback"))
                    .rewritePath("/orders/(?<segment>.*)", "/${segment}"))
                .uri("http://localhost:9000"))

            .route("youtube", spec -> spec.path("/youtube")
                .uri("https://youtube.com"))
            .build();
    }
}
