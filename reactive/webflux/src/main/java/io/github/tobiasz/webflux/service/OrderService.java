package io.github.tobiasz.webflux.service;

import io.github.tobiasz.webflux.dto.OrderDto;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<OrderDto> getOrderById(Integer id);

}
