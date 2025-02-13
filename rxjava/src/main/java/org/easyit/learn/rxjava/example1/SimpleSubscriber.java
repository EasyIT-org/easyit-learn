package org.easyit.learn.rxjava.example1;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimpleSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        // 请求第一个数据项
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        // 处理接收到的数据项
        System.out.println("Received item: " + item);
        // 请求下一个数据项
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        // 处理错误
        System.err.println("Error occurred: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        // 处理完成信号
        System.out.println("Processing complete.");
    }
}
