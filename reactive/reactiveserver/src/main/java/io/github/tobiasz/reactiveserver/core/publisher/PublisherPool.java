package io.github.tobiasz.reactiveserver.core.publisher;

import io.github.tobiasz.reactiveserver.core.pool.ResilientThreadPool;

public class PublisherPool {

    private static final ResilientThreadPool RESILIENT_THREAD_POOL = new ResilientThreadPool(10, 10);

    public static void addPublisher(Publisher<?> publisher) {
        RESILIENT_THREAD_POOL.addToPool(publisher);
    }
}
