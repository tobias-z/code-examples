package io.github.tobiasz.webflux.service.impl;

import io.github.tobiasz.webflux.dto.OrderDto;
import io.github.tobiasz.webflux.service.OrderService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Mono<OrderDto> getOrderById(Integer id) {
        return Mono.just(id)
            .map(this::getOrder)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .switchIfEmpty(Mono.error(new RuntimeException("no order found with that name")));
    }

    private Optional<OrderDto> getOrder(Integer id) {
        List<OrderDto> orderList = List.of(
            new OrderDto(1, "bob"),
            new OrderDto(2, "thebuilder")
        );

        return orderList
            .stream()
            .filter(orderDto -> orderDto.orderId().equals(id))
            .findFirst();
    }
}
