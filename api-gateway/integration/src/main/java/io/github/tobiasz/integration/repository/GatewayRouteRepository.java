package io.github.tobiasz.integration.repository;

import io.github.tobiasz.integration.entity.GatewayRoute;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRouteRepository extends ReactiveMongoRepository<GatewayRoute, String> {

}
