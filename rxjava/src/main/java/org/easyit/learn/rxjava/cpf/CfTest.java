package org.easyit.learn.rxjava.cpf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CfTest {

    private volatile static long startTime;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        start();
        List<CompletableFuture<Integer>> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            final int sleepTime = i;
            CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(
                () -> sleep(sleepTime), executorService);
            list.add(cf);
        }
        AtomicInteger sum1 = new AtomicInteger(0);
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
        sleep(4);
        CompletableFuture<Integer> sumcf = voidCompletableFuture.thenApplyAsync(
            (v) -> {
                int sum = 0;
                for (final CompletableFuture<Integer> integerCompletableFuture : list) {
                    try {
                        sum += integerCompletableFuture.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                sum1.set(sum);
                return sum;
            }, executorService
        );
        System.out.println(sumcf.get());
        printNow();

    }

    public static Integer sleep(int time) {
        try {
            SECONDS.sleep(time);
            printNow();
            return time;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void start() {
        startTime = System.nanoTime();
    }

    public static void printNow() {
        long l = System.nanoTime();
        long diff = l - startTime;
        long millis = TimeUnit.NANOSECONDS.toMillis(diff);
        System.out.println(millis);
    }
}


