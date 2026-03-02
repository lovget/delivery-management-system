package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
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

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/search")
    public List<OrderDto> getOrdersByStatus(@RequestParam String status) {
        return orderService.getOrdersByStatus(status);
    }

    @PostMapping
    public OrderDto createOrder(@RequestBody OrderCreateDto dto) {

        var order = orderService.createOrder(
                dto.getCustomerId(),
                dto.getProductIds(),
                dto.getStatus(),
                dto.getPaymentMethod()
        );

        return new OrderDto(
                order.getId(),
                order.getCustomer().getName(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }
}