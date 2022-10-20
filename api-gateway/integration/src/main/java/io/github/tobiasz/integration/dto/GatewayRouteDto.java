package io.github.tobiasz.integration.dto;


import io.github.tobiasz.integration.entity.GatewayRoute;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRouteDto {

    private String id;

    @NotBlank
    private String requestPath;

    @NotBlank
    private String forwardUri;

    public static GatewayRouteDto fromEntity(GatewayRoute gatewayRoute) {
        return GatewayRouteDto.builder()
            .id(gatewayRoute.getId())
            .requestPath(gatewayRoute.getRequestPath())
            .forwardUri(gatewayRoute.getForwardUri())
            .build();
    }
}
