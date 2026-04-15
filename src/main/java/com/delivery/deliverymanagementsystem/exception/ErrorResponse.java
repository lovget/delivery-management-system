package com.delivery.deliverymanagementsystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private List<String> details;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
        this(timestamp, status, error, path, null);
    }

    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         String path,
                         List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getPath() { return path; }
    public List<String> getDetails() { return details; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setPath(String path) { this.path = path; }
    public void setDetails(List<String> details) { this.details = details; }
}