package io.github.tobiasz.reactiveserver.server;

import io.github.tobiasz.reactiveserver.request.ServerRequest;

@FunctionalInterface
public interface ReactiveEndpoint<T> {

    T onPublish(ServerRequest serverRequest);

}

