package io.github.tobiasz.basicgateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/catch-all-fallback")
    public FallbackDto catchAllFallback() {
        return new FallbackDto(404, "That endpoint does not exist");
    }

    public record FallbackDto(Integer code, String message) {

    }

}
