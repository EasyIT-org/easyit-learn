package org.easyit.learn.rxjava.example1;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleProcessor<T, R> implements Processor<T, R> {

    private Subscriber<? super R> subscriber;
    private Subscription subscription;

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        // 处理数据项并转换类型
        R processedItem = (R) item; // 这里假设 T 和 R 是兼容的类型
        subscriber.onNext(processedItem);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}