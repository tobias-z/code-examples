package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResilientThreadPool {

    private final Map<Thread, ReactiveRunnable> reactiveRunnableMap = new HashMap<>();

    private final int defaultPoolSize;

    public ResilientThreadPool(int defaultPoolSize, int maxPublishersPerThread) {
        this.defaultPoolSize = defaultPoolSize;
        this.init(maxPublishersPerThread);
    }

    private void init(int maxPublishersPerThread) {
        for (int i = 0; i < this.defaultPoolSize; i++) {
            ReactiveRunnable runnable = new ReactiveRunnable(maxPublishersPerThread);
            Thread thread = new Thread(runnable);
            this.reactiveRunnableMap.put(thread, runnable);
            thread.start();
        }
    }

    public void addToPool(Publisher<?> publisher) {
        synchronized (this.reactiveRunnableMap) {
            for (Entry<Thread, ReactiveRunnable> entry : this.reactiveRunnableMap.entrySet()) {
                Thread activeThread = entry.getKey();
                ReactiveRunnable runnable = entry.getValue();
                if (runnable.canHandlePublisher()) {
                    publisher.onComplete((unused) -> {
                        if (runnable.activePublisherSize() == 0 && this.reactiveRunnableMap.size() > this.defaultPoolSize) {
                            activeThread.interrupt();
                            synchronized (this.reactiveRunnableMap) {
                                this.reactiveRunnableMap.remove(activeThread);
                            }
                        }
                    });
                    runnable.addPublisher(publisher);
                    break;
                }
            }
        }
    }

}
