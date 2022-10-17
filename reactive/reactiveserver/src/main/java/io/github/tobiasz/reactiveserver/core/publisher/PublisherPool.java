package io.github.tobiasz.reactiveserver.core.publisher;

import io.github.tobiasz.reactiveserver.core.pool.ResilientReactiveThreadPool;

public class PublisherPool {

    private static final ResilientReactiveThreadPool RESILIENT_THREAD_POOL = new ResilientReactiveThreadPool(10, 5);

    public static void addPublisher(Publisher<?> publisher) {
        RESILIENT_THREAD_POOL.addToPool(publisher);
    }
}
