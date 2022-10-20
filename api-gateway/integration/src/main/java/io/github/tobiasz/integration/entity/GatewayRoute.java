package io.github.tobiasz.integration.entity;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gateway_route")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GatewayRoute {

    @Id
    private String id;

    @Indexed(unique = true)
    private String requestPath;

    private String forwardUri;

    public static GatewayRoute fromDto(GatewayRouteDto gatewayRouteDto) {
        return GatewayRoute.builder()
            .forwardUri(gatewayRouteDto.getForwardUri())
            .requestPath(gatewayRouteDto.getRequestPath())
            .build();
    }
}
