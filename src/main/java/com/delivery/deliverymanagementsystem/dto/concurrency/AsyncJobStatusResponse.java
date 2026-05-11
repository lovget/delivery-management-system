package com.delivery.deliverymanagementsystem.dto.concurrency;

public record AsyncJobStatusResponse(String taskId,
                                     String status,
                                     Integer processedItems,
                                     String message) {
}
