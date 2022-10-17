package io.github.tobiasz.reactiveserver.core.publisher;

import io.github.tobiasz.reactiveserver.core.pool.ResilientReactiveThreadPool;
import io.github.tobiasz.reactiveserver.core.subscription.CoreSubscription;
import io.github.tobiasz.reactiveserver.core.subscription.CoreSubscription.SubscriptionType;
import io.github.tobiasz.reactiveserver.core.subscription.MappingSubscription;
import io.github.tobiasz.reactiveserver.core.subscription.Subscription;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Mono<T> implements Publisher<T> {

    private final List<CoreSubscription<T>> coreSubscriptionList = new ArrayList<>();
    private T data;

    public static <T> Mono<T> just(T data) {
        return new Mono<>(data);
    }

    @Override
    public Publisher<T> subscribe(Subscription<T> subscription) {
        this.coreSubscriptionList.add(new CoreSubscription<>(subscription, SubscriptionType.STANDARD));
        this.notifyPublisherThread();
        return this;
    }

    @Override
    public void onComplete(Subscription<T> subscription) {
        this.coreSubscriptionList.add(new CoreSubscription<>(subscription, SubscriptionType.COMPLETION));
        this.notifyPublisherThread();
    }

    @Override
    public <K> Publisher<K> map(MappingSubscription<T, K> mappingSubscription) {
        // TODO: fix this
        this.coreSubscriptionList.add(new CoreSubscription<>(data1 -> {
            K k = mappingSubscription.doMap(data1);
            this.data = (T) k;
        }, SubscriptionType.STANDARD));
        this.notifyPublisherThread();
        return (Publisher<K>) this;
    }

    @Override
    public void build() {
        PublisherPool.addPublisher(this);
        this.notifyPublisherThread();
    }

    @Override
    public boolean publish() {
        this.publish(SubscriptionType.STANDARD);
        boolean hasCompleted = this.hasSubscriptionType(SubscriptionType.COMPLETION);
        this.publish(SubscriptionType.COMPLETION);
        return hasCompleted && this.coreSubscriptionList.size() == 0;
    }

    private void notifyPublisherThread() {
        synchronized (ResilientReactiveThreadPool.class) {
            ResilientReactiveThreadPool.class.notifyAll();
        }
    }

    private boolean hasSubscriptionType(SubscriptionType subscriptionType) {
        for (CoreSubscription<T> coreSubscriptions : new ArrayList<>(this.coreSubscriptionList)) {
            if (coreSubscriptions.isType(subscriptionType)) {
                return true;
            }
        }
        return false;
    }

    private void publish(SubscriptionType subscriptionType) {
        for (CoreSubscription<T> coreSubscription : new ArrayList<>(this.coreSubscriptionList)) {
            if (coreSubscription.isType(subscriptionType)) {
                coreSubscription.publish(this.data);
                this.coreSubscriptionList.remove(coreSubscription);
            }
        }
    }

    @Override
    public String toString() {
        return """
            size: %s,
            data: %s,
            list: %s,
            """.formatted(this.coreSubscriptionList.size(), this.data, this.coreSubscriptionList);
    }
}
