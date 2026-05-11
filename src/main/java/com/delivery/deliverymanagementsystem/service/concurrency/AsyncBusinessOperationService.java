package com.delivery.deliverymanagementsystem.service.concurrency;

import com.delivery.deliverymanagementsystem.dto.concurrency.AsyncJobStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AsyncBusinessOperationService {

    private final Map<String, AsyncJobStatusResponse> jobs = new ConcurrentHashMap<>();
    private final AsyncBusinessOperationExecutor asyncBusinessOperationExecutor;

    public AsyncBusinessOperationService(AsyncBusinessOperationExecutor asyncBusinessOperationExecutor) {
        this.asyncBusinessOperationExecutor = asyncBusinessOperationExecutor;
    }

    public String startBusinessOperation(int itemsToProcess) {
        String taskId = UUID.randomUUID().toString();
        jobs.put(taskId, new AsyncJobStatusResponse(taskId, "IN_PROGRESS", 0, "Task started"));
        asyncBusinessOperationExecutor.execute(() -> runBusinessOperation(taskId, itemsToProcess));
        return taskId;
    }

    public AsyncJobStatusResponse getStatus(String taskId) {
        AsyncJobStatusResponse status = jobs.get(taskId);
        if (status == null) {
            throw new ResponseStatusException(NOT_FOUND, "Task not found");
        }
        return status;
    }

    private void runBusinessOperation(String taskId, int itemsToProcess) {
        try {
            for (int i = 1; i <= itemsToProcess; i++) {
                Thread.sleep(50L);
                jobs.put(taskId, new AsyncJobStatusResponse(taskId, "IN_PROGRESS", i,
                        "Processed " + i + " of " + itemsToProcess));
            }
            jobs.put(taskId, new AsyncJobStatusResponse(taskId, "DONE", itemsToProcess, "Task completed"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            jobs.put(taskId, new AsyncJobStatusResponse(taskId, "FAILED", null, "Task interrupted"));
        } catch (Exception e) {
            jobs.put(taskId, new AsyncJobStatusResponse(taskId, "FAILED", null, e.getMessage()));
        }
    }
}
