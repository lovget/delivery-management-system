package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import java.util.Objects;

public class OrderFilter {
    private OrderStatus status;
    private double amount;

    public OrderFilter(OrderStatus status, double amount) {
        this.status = status;
        this.amount = amount;
    }

    public OrderStatus getStatus() { return status; }
    public double getAmount() { return amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderFilter)) return false;
        OrderFilter that = (OrderFilter) o;
        return Double.compare(that.amount, amount) == 0 && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, amount);
    }
}