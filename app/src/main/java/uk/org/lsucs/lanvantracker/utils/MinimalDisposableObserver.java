package uk.org.lsucs.lanvantracker.utils;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by Zack on 11/9/2017.
 */

public class MinimalDisposableObserver<T> extends DisposableObserver<T> {
    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {
    }
}
