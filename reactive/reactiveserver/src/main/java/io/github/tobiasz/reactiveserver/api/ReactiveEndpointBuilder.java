package io.github.tobiasz.reactiveserver.api;

import io.github.tobiasz.reactiveserver.request.RequestMethod;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReactiveEndpointBuilder {

    private final List<CreatedEndpoint> createdEndpointList = new ArrayList<>();

    public <T> EndpointBuilder<T> endpoint() {
        return new EndpointBuilder<>();
    }

    public class EndpointBuilder<T> {

        private final CreatedEndpoint.CreatedEndpointBuilder endpointBuilder = CreatedEndpoint.builder();

        public EndpointBuilder<T> reactiveEndpoint(ReactiveEndpoint<T> reactiveEndpoint) {
            this.endpointBuilder.reactiveEndpoint(reactiveEndpoint);
            return this;
        }

        public EndpointBuilder<T> path(String path) {
            this.endpointBuilder.path(path);
            return this;
        }

        public EndpointBuilder<T> requestMethod(RequestMethod requestMethod) {
            this.endpointBuilder.requestMethod(requestMethod);
            return this;
        }

        public ReactiveEndpointBuilder createEndpoint() {
            ReactiveEndpointBuilder.this.createdEndpointList.add(this.endpointBuilder.build());
            return ReactiveEndpointBuilder.this;
        }
    }

    @Builder
    @Getter
    public static class CreatedEndpoint {

        private final ReactiveEndpoint<?> reactiveEndpoint;
        private final String path;
        private final RequestMethod requestMethod;
    }
}
