package io.github.tobiasz.integration.service.impl;

import io.github.tobiasz.integration.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void refreshRoutes() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
