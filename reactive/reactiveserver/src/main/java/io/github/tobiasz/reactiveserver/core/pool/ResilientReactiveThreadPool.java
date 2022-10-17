package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ResilientReactiveThreadPool {

    private final Map<Thread, ReactiveRunnable> reactiveRunnableMap = new ConcurrentHashMap<>();

    private final int defaultPoolSize;
    private final int maxPublishersPerThread;

    public ResilientReactiveThreadPool(int defaultPoolSize, int maxPublishersPerThread) {
        this.defaultPoolSize = defaultPoolSize;
        this.maxPublishersPerThread = maxPublishersPerThread;
        this.init();
    }

    private void init() {
        for (int i = 0; i < this.defaultPoolSize; i++) {
            this.newThreadInPool();
        }
    }

    private void newThreadInPool() {
        synchronized (this.reactiveRunnableMap) {
            ReactiveRunnable runnable = new ReactiveRunnable(this.maxPublishersPerThread);
            Thread thread = new Thread(runnable);
            this.reactiveRunnableMap.put(thread, runnable);
            thread.start();
        }
    }

    public void addToPool(Publisher<?> publisher) {
        // TODO: should be implemented using a load balancer strategy
        for (Entry<Thread, ReactiveRunnable> entry : this.reactiveRunnableMap.entrySet()) {
            Thread activeThread = entry.getKey();
            ReactiveRunnable runnable = entry.getValue();
            if (runnable.canHandlePublisher()) {
                publisher.onComplete((unused) -> {
                    if (runnable.activePublisherSize() == 0 && this.reactiveRunnableMap.size() > this.defaultPoolSize) {
                        activeThread.interrupt();
                        this.reactiveRunnableMap.remove(activeThread);
                    }
                });
                runnable.addPublisher(publisher);
                return;
            }
        }
        this.newThreadInPool();
        this.addToPool(publisher);
    }

}
