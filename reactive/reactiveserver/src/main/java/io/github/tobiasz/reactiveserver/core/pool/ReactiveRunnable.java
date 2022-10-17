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
                System.out.println("size: " + this.publisherList.size());
                synchronized (this.publisherList) {
                    new ArrayList<>(this.publisherList).stream()
                        .filter(Objects::nonNull)
                        .filter(Publisher::publish)
                        .forEach(this::remove);
                }
            }
        }
    }

    private void remove(Publisher<?> publisher) {
        boolean success = this.publisherList.remove(publisher);
        System.out.println("removed: " + publisher.toString() + ", success: " + success);
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
