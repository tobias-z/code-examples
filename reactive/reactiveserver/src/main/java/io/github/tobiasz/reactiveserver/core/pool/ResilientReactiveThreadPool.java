package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResilientReactiveThreadPool {

    // There is prob a more optimal solution that using this
    private final List<PoolObject> poolObjectMap = new CopyOnWriteArrayList<>();
    private volatile int nextPoolIndex = 0;

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
        ReactiveRunnable runnable = new ReactiveRunnable(this.maxPublishersPerThread);
        Thread thread = new Thread(runnable);
        this.poolObjectMap.add(new PoolObject(thread, runnable));
        thread.start();
    }

    public void addToPool(Publisher<?> publisher) {
        PoolObject poolObject = this.poolObjectMap.get(this.nextPoolIndex);
        this.setNextPoolIndex();
        Thread activeThread = poolObject.thread();
        ReactiveRunnable runnable = poolObject.reactiveRunnable();
        if (runnable.canHandlePublisher()) {
            publisher.onComplete((unused) -> {
                if (runnable.activePublisherSize() == 0 && this.poolObjectMap.size() > this.defaultPoolSize) {
                    activeThread.interrupt();
                    this.poolObjectMap.remove(poolObject);
                    this.setNextPoolIndex();
                }
            });
            runnable.addPublisher(publisher);
            return;
        }
        synchronized (this.poolObjectMap) {
            this.newThreadInPool();
        }
        this.addToPool(publisher);
    }

    /**
     * Other alternatives could be considered, but a simple Round-Robin does the job
     */
    private synchronized void setNextPoolIndex() {
        if (this.nextPoolIndex + 1 < this.poolObjectMap.size()) {
            this.nextPoolIndex++;
        } else {
            this.nextPoolIndex = 0;
        }
    }

}
