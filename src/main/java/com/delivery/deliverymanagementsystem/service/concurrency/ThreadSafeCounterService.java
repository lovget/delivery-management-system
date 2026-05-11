package com.delivery.deliverymanagementsystem.service.concurrency;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ThreadSafeCounterService {

    private final AtomicInteger counter = new AtomicInteger(0);

    public int incrementAndGet() {
        return counter.incrementAndGet();
    }

    public int getValue() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}
