package io.github.tobiasz.reactiveserver.core.subscription;

@FunctionalInterface
public interface MappingSubscription<T, K> {

    K doMap(T data);

}
