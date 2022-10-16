package io.github.tobiasz.reactiveserver.core.publisher;

import io.github.tobiasz.reactiveserver.core.subscription.MappingSubscription;
import io.github.tobiasz.reactiveserver.core.subscription.Subscription;

public interface Publisher<T> {

    Publisher<T> subscribe(Subscription<T> subscription);

    Publisher<T> onComplete(Subscription<T> subscription);

    <K> Publisher<K> map(MappingSubscription<T, K> mappingSubscription);

    void build();

    void publish();

}
