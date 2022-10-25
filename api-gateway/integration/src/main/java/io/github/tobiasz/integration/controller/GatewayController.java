package io.github.tobiasz.integration.controller;

import static io.github.tobiasz.integration.Constants.CATCH_ALL_FALLBACK;

import io.github.tobiasz.integration.dto.CreatedDto;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.service.GatewayRouteService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayRouteService gatewayRouteService;

    @PostMapping("/gateway/route")
    public Mono<CreatedDto<String>> createGatewayRoute(@RequestBody @Valid GatewayRouteDto gatewayRouteDto) {
        return gatewayRouteService.createGatewayRoute(gatewayRouteDto);
    }

    @GetMapping("/gateway/route")
    public Flux<GatewayRouteDto> getAllRoutes() {
        return gatewayRouteService.getAllGatewayRoutes();
    }

    @GetMapping(CATCH_ALL_FALLBACK)
    public ResponseEntity<FallbackDto> catchAllFallback() {
        int status = 404;
        return ResponseEntity
            .status(status)
            .body(new FallbackDto(status, "That endpoint does not exist"));
    }

    public record FallbackDto(Integer code, String message) {

    }
}
