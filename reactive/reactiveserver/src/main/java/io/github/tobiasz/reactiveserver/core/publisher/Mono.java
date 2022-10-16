package io.github.tobiasz.reactiveserver.core.publisher;

import io.github.tobiasz.reactiveserver.core.pool.ResilientThreadPool;
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
    public Publisher<T> onComplete(Subscription<T> subscription) {
        this.coreSubscriptionList.add(new CoreSubscription<>(subscription, SubscriptionType.COMPLETION));
        this.notifyPublisherThread();
        return this;
    }

    @Override
    public <K> Publisher<K> map(MappingSubscription<T, K> mappingSubscription) {
        K k = mappingSubscription.doMap(this.data);
        this.data = (T) k;
        this.notifyPublisherThread();
        return (Publisher<K>) this;
    }

    @Override
    public void build() {
        PublisherPool.addPublisher(this);
        this.notifyPublisherThread();
    }

    private void notifyPublisherThread() {
        synchronized (ResilientThreadPool.class) {
            ResilientThreadPool.class.notifyAll();
        }
    }

    @Override
    public void publish() {
        if (this.hasStandardSubscriptions()) {
            this.publish(SubscriptionType.STANDARD);
            this.publish(SubscriptionType.COMPLETION);
        }
    }

    private boolean hasStandardSubscriptions() {
        for (CoreSubscription<T> coreSubscriptions : new ArrayList<>(this.coreSubscriptionList)) {
            if (coreSubscriptions.isType(SubscriptionType.STANDARD)) {
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
}
