package io.github.tobiasz.integration.controller;

import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.service.GatewayRouteService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("gateway")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayRouteService gatewayRouteService;

    @PostMapping("/new-route")
    public Mono<CreatedDto<String>> createGatewayRoute(@RequestBody @Valid GatewayRouteDto gatewayRouteDto) {
        return this.gatewayRouteService.createGatewayRoute(gatewayRouteDto);
    }
}
