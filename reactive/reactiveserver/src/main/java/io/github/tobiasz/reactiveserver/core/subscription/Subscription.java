package io.github.tobiasz.reactiveserver.core.subscription;

@FunctionalInterface
public interface Subscription<T> {

    void onPublish(T data);

}
