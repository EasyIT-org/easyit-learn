package org.easyit.learn.rxjava.main;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.exceptions.MissingBackpressureException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class TestMain {

    public static void main(String[] args) {
        MySubscriber mySubscriber = new MySubscriber();
        Flowable.just("abc","def").subscribe(mySubscriber);
    }

    public static class MySubscriber implements FlowableSubscriber<String> {

        @Override
        public void onSubscribe(final Subscription s) {
            System.out.println("onSubscribe");
            s.request(1);
        }

        @Override
        public void onNext(final String s) {
            System.out.println("onNext");
            System.out.println(s);

        }

        @Override
        public void onError(final Throwable t) {
            System.out.println("onError");
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete");
        }
    }

    public static void println(String s){
        System.out.println(s);
    }

    private static void groupByBackPressure() {
        Flowable.range(1, 1000)
                .groupBy(v -> v)
                .flatMap(v -> v, 16)
                .test()
                .assertError(MissingBackpressureException.class);
    }
}
