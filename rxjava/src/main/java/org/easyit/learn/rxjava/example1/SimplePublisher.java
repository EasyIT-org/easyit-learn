package org.easyit.learn.rxjava.example1;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SimplePublisher<T> implements Publisher<T> {

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        // 创建一个 Subscription 对象并传递给 Subscriber
        SimpleSubscription subscription = new SimpleSubscription(subscriber, () -> emit());
        subscriber.onSubscribe(subscription);
    }

    public T emit() {
        T item = (T) new Object();
        return item;
    }

    private static class SimpleSubscription<T> implements Subscription {
        private final Subscriber<? super T> subscriber;
        private final Supplier<T> supplier;
        private boolean isCanceled = false;
        private AtomicInteger counter = new AtomicInteger(0);

        public SimpleSubscription(Subscriber<? super T> subscriber, Supplier<T> supplier) {
            this.subscriber = subscriber;
            this.supplier = supplier;
        }

        @Override
        public void request(long n) {
            if (isCanceled) {
                return;
            }

            for (long i = 0; i < n; i++) {
                if (isCanceled) {
                    break;
                }

                // 模拟数据生成
                if (counter.getAndIncrement() < 5) {
                    T item = supplier.get();
                    subscriber.onNext(item);
                    return;
                }

            }
            // 数据发送完成后调用 onComplete
            if (!isCanceled) {
                subscriber.onComplete();
            }
        }

        @Override
        public void cancel() {
            isCanceled = true;
        }
    }
}
