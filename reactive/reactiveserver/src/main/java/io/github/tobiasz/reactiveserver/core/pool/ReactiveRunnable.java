package io.github.tobiasz.reactiveserver.core.pool;

import io.github.tobiasz.reactiveserver.core.publisher.Publisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveRunnable implements Runnable {

    private final List<Publisher<?>> publisherList = new ArrayList<>();

    private final int maxPublishers;

    public boolean canHandlePublisher() {
        return this.maxPublishers > this.activePublisherSize();
    }

    public void addPublisher(Publisher<?> publisher) {
        synchronized (this.publisherList) {
            this.publisherList.add(publisher);
        }
    }

    public int activePublisherSize() {
        synchronized (this.publisherList) {
            return this.publisherList.size();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            this.await();
            if (this.publisherList.size() > 0) {
                synchronized (this.publisherList) {
                    new ArrayList<>(this.publisherList).stream()
                        .filter(Objects::nonNull)
                        .filter(Publisher::publish)
                        .forEach(this.publisherList::remove);
                }
            }
        }
    }

    private void await() {
        synchronized (ResilientReactiveThreadPool.class) {
            try {
                ResilientReactiveThreadPool.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}
