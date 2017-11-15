package uk.org.lsucs.lanvantracker.utils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by Zack on 11/10/2017.
 */

public class MinimalSubscriber<T> implements Subscriber<T> {
    @Override
    public void onSubscribe(Subscription s) {
    }

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }
}
