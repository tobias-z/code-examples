package io.github.tobiasz.reactiveserver;

import io.github.tobiasz.reactiveserver.api.ReactiveEndpointBuilder;
import io.github.tobiasz.reactiveserver.request.RequestMethod;
import io.github.tobiasz.reactiveserver.server.ReactiveServer;

public class ReactiveserverApplication {

    public static void main(String[] args) {
        ReactiveEndpointBuilder reactiveEndpointBuilder = getReactiveEndpointBuilder();
        try (ReactiveServer server = new ReactiveServer("localhost", 8080, reactiveEndpointBuilder)) {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ReactiveEndpointBuilder getReactiveEndpointBuilder() {
        return new ReactiveEndpointBuilder()
            .endpoint()
            .path("/hello-world")
            .requestMethod(RequestMethod.GET)
            .reactiveEndpoint(serverRequest -> "hello world")
            .createEndpoint();
    }

}
