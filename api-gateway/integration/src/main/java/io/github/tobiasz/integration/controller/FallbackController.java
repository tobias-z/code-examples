package io.github.tobiasz.integration.controller;

import static io.github.tobiasz.integration.config.RouteConstants.CATCH_ALL_FALLBACK;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping(CATCH_ALL_FALLBACK)
    public FallbackDto catchAllFallback() {
        return new FallbackDto(404, "That endpoint does not exist");
    }

    public record FallbackDto(Integer code, String message) {

    }

}
