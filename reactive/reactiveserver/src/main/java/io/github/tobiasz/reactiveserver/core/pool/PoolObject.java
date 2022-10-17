package io.github.tobiasz.reactiveserver.core.pool;


public record PoolObject(Thread thread, ReactiveRunnable reactiveRunnable) {

}
