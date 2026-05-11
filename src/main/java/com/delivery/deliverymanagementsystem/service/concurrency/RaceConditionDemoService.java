package com.delivery.deliverymanagementsystem.service.concurrency;

import com.delivery.deliverymanagementsystem.dto.concurrency.RaceConditionDemoResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RaceConditionDemoService {

    public RaceConditionDemoResponse runDemo(int threads, int incrementsPerThread) {
        int unsafeActual = runUnsafeCounter(threads, incrementsPerThread);
        int safeActual = runSafeCounter(threads, incrementsPerThread);
        int expected = threads * incrementsPerThread;
        return new RaceConditionDemoResponse(threads, incrementsPerThread, expected, unsafeActual, safeActual);
    }

    private int runUnsafeCounter(int threads, int incrementsPerThread) {
        class MutableInt {
            private int value = 0;
        }

        MutableInt counter = new MutableInt();
        runConcurrentIncrement(threads, incrementsPerThread, () -> counter.value++);
        return counter.value;
    }

    private int runSafeCounter(int threads, int incrementsPerThread) {
        AtomicInteger counter = new AtomicInteger(0);
        runConcurrentIncrement(threads, incrementsPerThread, counter::incrementAndGet);
        return counter.get();
    }

    private void runConcurrentIncrement(int threads, int incrementsPerThread, Runnable operation) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        operation.run();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
}
