package com.delivery.deliverymanagementsystem.service.concurrency;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncBusinessOperationExecutor {

    @Async("labExecutor")
    public CompletableFuture<Void> execute(Runnable operation) {
        operation.run();
        return CompletableFuture.completedFuture(null);
    }
}