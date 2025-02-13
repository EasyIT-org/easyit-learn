package org.easyit.learn.rxjava.example1;

public class ReactivexModel {

    public static void main(String[] args) {
        // 创建 Publisher
        SimplePublisher<Object> publisher = new SimplePublisher<>();

        // 创建 Subscriber
        SimpleSubscriber<Object> subscriber = new SimpleSubscriber<>();

        // 创建 Processor
        SimpleProcessor<Object, Object> processor = new SimpleProcessor<>();

        // 将 Subscriber 订阅到 Processor
        processor.subscribe(subscriber);

        // 将 Processor 订阅到 Publisher
        publisher.subscribe(processor);
    }
}
