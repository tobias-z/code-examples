package io.github.tobiasz.reactiveserver;

import io.github.tobiasz.reactiveserver.request.RequestMethod;
import io.github.tobiasz.reactiveserver.server.ReactiveEndpointBuilder;
import io.github.tobiasz.reactiveserver.server.ReactiveServer;

public class ReactiveserverApplication {

    public static void main(String[] args) {
        ReactiveEndpointBuilder reactiveEndpointBuilder = getReactiveEndpointBuilder();
        try (ReactiveServer server = new ReactiveServer("0.0.0.0", 9000, reactiveEndpointBuilder)) {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static ReactiveEndpointBuilder getReactiveEndpointBuilder() {
        return new ReactiveEndpointBuilder()
            .endpoint()
            .path("/hello-world")
            .requestMethod(RequestMethod.GET)
            .reactiveEndpoint(serverRequest -> "hello world")
            .createEndpoint()

            .endpoint()
            .path("/example")
            .requestMethod(RequestMethod.GET)
            .reactiveEndpoint(serverRequest -> {
                String name = serverRequest.getQueryParams().get("name");
                return new Person(name);
            })
            .createEndpoint()

            .endpoint()
            .path("/something")
            .requestMethod(RequestMethod.GET)
            .reactiveEndpoint(serverRequest -> 100)
            .createEndpoint();
    }

    record Person(String name) {

    }

}
