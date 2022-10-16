package io.github.tobiasz.reactiveserver.core.subscription;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoreSubscription<T> {

    private final Subscription<T> subscription;
    private final SubscriptionType subscriptionType;

    public void publish(T data) {
        this.subscription.onPublish(data);
    }

    public boolean isType(SubscriptionType type) {
        return this.subscriptionType.equals(type);
    }

    public enum SubscriptionType {
        STANDARD,
        COMPLETION,
    }

}
