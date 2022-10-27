package io.github.tobiasz.integration.config.security.authmethod;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthMethod implements AuthMethod {

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Bearer");
    }

    @Override
    public Optional<Integer> authenticate(String token) {
        // TODO: Actually do something
        return Optional.empty();
    }

}
