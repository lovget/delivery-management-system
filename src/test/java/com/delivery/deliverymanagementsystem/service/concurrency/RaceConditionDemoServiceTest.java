package com.delivery.deliverymanagementsystem.service.concurrency;

import com.delivery.deliverymanagementsystem.dto.concurrency.RaceConditionDemoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService service = new RaceConditionDemoService();

    @Test
    void shouldShowSafeCounterAsExpected() {
        RaceConditionDemoResponse result = service.runDemo(60, 2000);

        assertEquals(120000, result.expected());
        assertEquals(result.expected(), result.safeActual());
        assertTrue(result.unsafeActual() <= result.expected());
    }
}
