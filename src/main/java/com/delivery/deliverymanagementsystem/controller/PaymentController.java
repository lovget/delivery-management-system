package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment create(@RequestBody Payment request) {
        Payment payment = new Payment();
        payment.setMethod(request.getMethod());
        payment.setOrder(request.getOrder());
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