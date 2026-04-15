package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public Order getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter orders by status and minimum amount")
    public List<Order> filter(@RequestParam OrderStatus status,
                              @RequestParam double amount) {
        return orderService.getFiltered(status, amount);
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Filter orders by status and minimum amount (native SQL)")
    public List<Order> filterNative(@RequestParam OrderStatus status,
                                    @RequestParam double amount) {
        return orderService.getFilteredNative(status, amount);
    }

    @GetMapping("/filter/by-customer")
    @Operation(summary = "Filter orders by customer name and minimum amount")
    public List<Order> filterByCustomer(@RequestParam String name,
                                        @RequestParam double amount) {
        return orderService.getByCustomerName(name, amount);
    }

    @GetMapping("/filter/by-customer/native")
    @Operation(summary = "Filter orders by customer name and minimum amount (native SQL)")
    public List<Order> filterByCustomerNative(@RequestParam String name,
                                              @RequestParam double amount) {
        return orderService.getByCustomerNameNative(name, amount);
    }

    @GetMapping("/page")
    @Operation(summary = "Get paginated orders")
    public Page<Order> page(@RequestParam int page,
                            @RequestParam int size) {
        return orderService.getPaged(page, size);
    }

    @PostMapping
    @Operation(summary = "Create order")
    public Order create(@Valid @RequestBody OrderCreateDto dto) {
        return orderService.createOrder(dto);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public Order updateStatus(@PathVariable Long id,
                              @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}