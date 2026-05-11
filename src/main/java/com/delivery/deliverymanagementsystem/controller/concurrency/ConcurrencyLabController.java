package com.delivery.deliverymanagementsystem.controller.concurrency;

import com.delivery.deliverymanagementsystem.dto.concurrency.AsyncJobStartResponse;
import com.delivery.deliverymanagementsystem.dto.concurrency.AsyncJobStatusResponse;
import com.delivery.deliverymanagementsystem.dto.concurrency.RaceConditionDemoResponse;
import com.delivery.deliverymanagementsystem.service.concurrency.AsyncBusinessOperationService;
import com.delivery.deliverymanagementsystem.service.concurrency.RaceConditionDemoService;
import com.delivery.deliverymanagementsystem.service.concurrency.ThreadSafeCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lab6/concurrency")
@Tag(name = "Lab6 Concurrency", description = "Операции для лабораторной №6")
public class ConcurrencyLabController {

    private final AsyncBusinessOperationService asyncBusinessOperationService;
    private final ThreadSafeCounterService threadSafeCounterService;
    private final RaceConditionDemoService raceConditionDemoService;

    public ConcurrencyLabController(AsyncBusinessOperationService asyncBusinessOperationService,
                                    ThreadSafeCounterService threadSafeCounterService,
                                    RaceConditionDemoService raceConditionDemoService) {
        this.asyncBusinessOperationService = asyncBusinessOperationService;
        this.threadSafeCounterService = threadSafeCounterService;
        this.raceConditionDemoService = raceConditionDemoService;
    }

    @PostMapping("/tasks")
    @Operation(summary = "Запустить асинхронную бизнес-операцию")
    public AsyncJobStartResponse startTask(@RequestParam(defaultValue = "30") int itemsToProcess) {
        String taskId = asyncBusinessOperationService.startBusinessOperation(itemsToProcess);
        return new AsyncJobStartResponse(taskId, "/lab6/concurrency/tasks/" + taskId);
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Получить статус асинхронной операции")
    public AsyncJobStatusResponse getTaskStatus(@PathVariable String taskId) {
        return asyncBusinessOperationService.getStatus(taskId);
    }

    @PostMapping("/counter/increment")
    @Operation(summary = "Потокобезопасно увеличить счетчик")
    public int incrementCounter() {
        return threadSafeCounterService.incrementAndGet();
    }

    @GetMapping("/counter")
    @Operation(summary = "Получить значение потокобезопасного счетчика")
    public int getCounter() {
        return threadSafeCounterService.getValue();
    }

    @PostMapping("/counter/reset")
    @Operation(summary = "Сбросить счетчик")
    public void resetCounter() {
        threadSafeCounterService.reset();
    }

    @GetMapping("/race-condition")
    @Operation(summary = "Демонстрация race condition и решения")
    public RaceConditionDemoResponse raceConditionDemo(@RequestParam(defaultValue = "50") int threads,
                                                       @RequestParam(defaultValue = "1000") int incrementsPerThread) {
        return raceConditionDemoService.runDemo(threads, incrementsPerThread);
    }
}