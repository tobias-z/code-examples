package io.github.tobiasz.integration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("io.github.tobiasz.security")
@Getter
@Setter
public class SecurityProperties {

    private String username;
    private String password;

}
