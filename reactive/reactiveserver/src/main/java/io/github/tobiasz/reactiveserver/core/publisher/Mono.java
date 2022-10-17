package io.github.tobiasz.reactiveserver.core.publisher;

import static java.util.Objects.isNull;

import io.github.tobiasz.reactiveserver.core.pool.ResilientReactiveThreadPool;
import io.github.tobiasz.reactiveserver.core.subscription.CoreSubscription;
import io.github.tobiasz.reactiveserver.core.subscription.CoreSubscription.SubscriptionType;
import io.github.tobiasz.reactiveserver.core.subscription.MappingSubscription;
import io.github.tobiasz.reactiveserver.core.subscription.Subscription;
import io.github.tobiasz.reactiveserver.core.util.VoidFunc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Optional<VoidFunc> publish() {
        this.publish(SubscriptionType.STANDARD);

        if (this.hasSubscriptionType(SubscriptionType.COMPLETION)) {
            return Optional.of(() -> this.publish(SubscriptionType.COMPLETION));
        }

        return Optional.empty();
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
                if (isNull(this.data) && !coreSubscription.isType(SubscriptionType.COMPLETION)) {
                    continue;
                }
                coreSubscription.publish(this.data);
                this.coreSubscriptionList.remove(coreSubscription);
            }
        }
    }
}
