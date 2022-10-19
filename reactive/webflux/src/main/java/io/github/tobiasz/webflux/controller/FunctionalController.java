package io.github.tobiasz.webflux.controller;

import io.github.tobiasz.webflux.dto.OrderDto;
import io.github.tobiasz.webflux.service.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FunctionalController {

    @Bean
    public RouterFunction<ServerResponse> exampleFunctionalEndpoints(OrderService orderService) {
        return RouterFunctions.route()
            .GET("/functional/{id}", request -> {
                int id = Integer.parseInt(request.pathVariable("id"));
                return ServerResponse.ok().body(orderService.getOrderById(id), OrderDto.class);
            })
            .build();
    }

}
