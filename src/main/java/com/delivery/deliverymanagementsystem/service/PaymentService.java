package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment update(Long id, Payment updated) {
        Payment payment = getById(id);
        payment.setMethod(updated.getMethod());
        payment.setAmount(updated.getAmount());
        payment.setPaidAt(updated.getPaidAt());
        return paymentRepository.save(payment);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}