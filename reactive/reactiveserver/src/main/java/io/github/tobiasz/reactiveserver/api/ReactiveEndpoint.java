package io.github.tobiasz.reactiveserver.api;

import io.github.tobiasz.reactiveserver.request.ServerRequest;

@FunctionalInterface
public interface ReactiveEndpoint<T> {

    T onPublish(ServerRequest serverRequest);

}
