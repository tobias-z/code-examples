package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResilientReactiveThreadPool {

    // There is prob a more optimal solution that using this
    private final List<PoolObject> poolObjectList = new CopyOnWriteArrayList<>();
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
        this.poolObjectList.add(new PoolObject(thread, runnable));
        thread.start();
    }

    public void addToPool(Publisher<?> publisher) {
        PoolObject poolObject = this.poolObjectList.get(this.nextPoolIndex);
        this.setNextPoolIndex();
        ReactiveRunnable runnable = poolObject.reactiveRunnable();
        publisher.onComplete((unused) -> {
            if (runnable.activePublisherSize() == 0 && this.poolObjectList.size() > this.defaultPoolSize) {
                poolObject.thread().interrupt();
                this.poolObjectList.remove(poolObject);
                this.setNextPoolIndex();
            }
        });
        runnable.addPublisher(publisher);
    }

    /**
     * Other alternatives could be considered, but a simple Round-Robin does the job
     */
    private synchronized void setNextPoolIndex() {
        this.trySetPoolIndex(0);
    }

    private synchronized void trySetPoolIndex(int tries) {
        int size = this.poolObjectList.size();
        if (tries >= size) {
            this.newThreadInPool();
            this.nextPoolIndex = size + 1;
        }
        if (this.nextPoolIndex + 1 < size) {
            this.nextPoolIndex++;
            if (!this.poolObjectList.get(this.nextPoolIndex).reactiveRunnable().canHandlePublisher()) {
                this.trySetPoolIndex(++tries);
            }
        } else {
            this.nextPoolIndex = 0;
        }
    }

}
