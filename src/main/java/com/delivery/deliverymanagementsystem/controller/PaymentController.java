package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.PaymentCreateDto;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.service.OrderService;
import com.delivery.deliverymanagementsystem.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Операции с платежами")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService,
                             OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Создать платеж")
    public Payment create(@Valid @RequestBody PaymentCreateDto dto) {

        Order order = orderService.getById(dto.getOrderId());

        Payment payment = new Payment();
        payment.setMethod(dto.getMethod());
        payment.setAmount(dto.getAmount());
        payment.setPaidAt(LocalDateTime.now());
        payment.setOrder(order);

        return paymentService.create(payment);
    }

    @GetMapping
    @Operation(summary = "Получить список платежей")
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить платеж по id")
    public Payment getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить платеж")
    public void delete(@PathVariable Long id) {
        paymentService.delete(id);
    }
}