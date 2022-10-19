package io.github.tobiasz.webflux.controller;

import io.github.tobiasz.webflux.dto.OrderDto;
import io.github.tobiasz.webflux.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oldschool")
@RequiredArgsConstructor
public class OldSchoolController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Mono<OrderDto> getOrder(@PathVariable Integer id) {
        return this.orderService.getOrderById(id);
    }

}
