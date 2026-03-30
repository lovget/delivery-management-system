package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.PaymentCreateDto;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.service.OrderService;
import com.delivery.deliverymanagementsystem.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService,
                             OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping
    public Payment create(@RequestBody PaymentCreateDto dto) {

        Order order = orderService.getById(dto.getOrderId());

        Payment payment = new Payment();
        payment.setMethod(dto.getMethod());
        payment.setAmount(dto.getAmount());
        payment.setOrder(order);

        return paymentService.create(payment);
    }

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paymentService.delete(id);
    }
}