package com.delivery.deliverymanagementsystem.dto;

import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import java.util.Objects;

public class OrderFilter {

    private final OrderStatus status;
    private final String customerName;
    private final double amount;
    private final String source;

    public OrderFilter(OrderStatus status, String customerName, double amount, String source) {
        this.status = status;
        this.customerName = customerName;
        this.amount = amount;
        this.source = source;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderFilter)) {
            return false;
        }
        OrderFilter that = (OrderFilter) o;
        return Double.compare(that.amount, amount) == 0
                && status == that.status
                && Objects.equals(customerName, that.customerName)
                && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, customerName, amount, source);
    }
}