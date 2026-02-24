package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.OrderDto;
import com.delivery.deliverymanagementsystem.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET http://localhost:8080/orders
    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    // GET http://localhost:8080/orders/1
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // GET http://localhost:8080/orders/search?status=CREATED
    @GetMapping("/search")
    public List<OrderDto> searchOrders(@RequestParam String status) {
        return orderService.getAllOrders()
                .stream()
                .filter(order -> order.getStatus().equalsIgnoreCase(status))
                .toList();
    }
}