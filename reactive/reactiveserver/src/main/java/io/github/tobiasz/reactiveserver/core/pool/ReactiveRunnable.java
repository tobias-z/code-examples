package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveRunnable implements Runnable {

    private final List<Publisher<?>> publisherList = new ArrayList<>();

    private final int maxPublishers;

    public synchronized boolean canHandlePublisher() {
        return this.maxPublishers > this.activePublisherSize();
    }

    public synchronized void addPublisher(Publisher<?> publisher) {
        this.publisherList.add(publisher);
    }

    public synchronized int activePublisherSize() {
        return this.publisherList.size();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            this.await();
            if (this.publisherList.size() > 0) {
                for (Publisher<?> publisher : new ArrayList<>(this.publisherList)) {
                    publisher.publish();
                }
            }
        }
    }

    private void await() {
        synchronized (ResilientThreadPool.class) {
            try {
                ResilientThreadPool.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}
