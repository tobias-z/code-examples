package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResilientReactiveThreadPool {

    private final Map<Thread, ReactiveRunnable> reactiveRunnableMap = new HashMap<>();

    private final int defaultPoolSize;
    private final int maxPublishersPerThread;
    private volatile int count;

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
        synchronized (this.reactiveRunnableMap) {
            System.out.println("trying to add to runnable: " + ++this.count + ", threadCount: " + this.reactiveRunnableMap.size());
            for (Entry<Thread, ReactiveRunnable> entry : this.reactiveRunnableMap.entrySet()) {
                Thread activeThread = entry.getKey();
                ReactiveRunnable runnable = entry.getValue();
                if (runnable.canHandlePublisher()) {
                    publisher.onComplete((unused) -> {
                        System.out.println("called onComplete");
                        if (runnable.activePublisherSize() == 0 && this.reactiveRunnableMap.size() > this.defaultPoolSize) {
                            activeThread.interrupt();
                            synchronized (this.reactiveRunnableMap) {
                                this.reactiveRunnableMap.remove(activeThread);
                            }
                        }
                    });
                    synchronized (this) {
                        runnable.addPublisher(publisher);
                        this.notifyAll();
                    }
                    return;
                }
            }
        }
        this.newThreadInPool();
        this.addToPool(publisher);
    }

}
