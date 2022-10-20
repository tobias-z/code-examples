package io.github.tobiasz.integration.dto;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRouteDto {

    @NotBlank
    private String requestPath;

    @NotBlank
    private String forwardUri;
}
