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

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }


    @GetMapping("/search")
    public List<OrderDto> getOrdersByStatus(@RequestParam String status) {
        return orderService.getOrdersByStatus(status);
    }


    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }
}