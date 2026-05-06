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
@Tag(name = "Orders", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Получить все заказы")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по id")
    public Order getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/filter")
    @Operation(summary = "Фильтр заказов по статусу и минимальной сумме")
    public List<Order> filter(@RequestParam OrderStatus status,
                              @RequestParam double amount) {
        return orderService.getFiltered(status, amount);
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Фильтр заказов по статусу и минимальной сумме (native SQL)")
    public List<Order> filterNative(@RequestParam OrderStatus status,
                                    @RequestParam double amount) {
        return orderService.getFilteredNative(status, amount);
    }

    @GetMapping("/filter/by-customer")
    @Operation(summary = "Фильтр заказов по имени клиента и минимальной сумме")
    public List<Order> filterByCustomer(@RequestParam String name,
                                        @RequestParam double amount) {
        return orderService.getByCustomerName(name, amount);
    }

    @GetMapping("/filter/by-customer/native")
    @Operation(summary = "Фильтр заказов по имени клиента и минимальной сумме (native SQL)")
    public List<Order> filterByCustomerNative(@RequestParam String name,
                                              @RequestParam double amount) {
        return orderService.getByCustomerNameNative(name, amount);
    }

    @GetMapping("/page")
    @Operation(summary = "Получить заказы постранично")
    public Page<Order> page(@RequestParam int page,
                            @RequestParam int size) {
        return orderService.getPaged(page, size);
    }

    @PostMapping
    @Operation(summary = "Создать заказ")
    public Order create(@Valid @RequestBody OrderCreateDto dto) {
        return orderService.createOrder(dto);
    }


    @PostMapping("/bulk")
    @Operation(summary = "Массовое создание заказов")
    public List<Order> createBulk(@Valid @RequestBody List<OrderCreateDto> dtos,
                                  @RequestParam(defaultValue = "true") boolean transactional) {
        if (transactional) {
            return orderService.createOrdersBulkTransactional(dtos);
        }

        return orderService.createOrdersBulkNonTransactional(dtos);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Обновить статус заказа")
    public Order updateStatus(@PathVariable Long id,
                              @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}