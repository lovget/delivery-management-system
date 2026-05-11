package com.delivery.deliverymanagementsystem.dto.concurrency;

public record RaceConditionDemoResponse(int threads,
                                        int incrementsPerThread,
                                        int expected,
                                        int unsafeActual,
                                        int safeActual) {
}
