package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void create_shouldSavePayment() {
        Payment payment = new Payment();
        payment.setMethod("CARD");

        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.create(payment);

        assertEquals("CARD", result.getMethod());
    }

    @Test
    void getAll_shouldReturnPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));

        assertEquals(1, paymentService.getAll().size());
    }

    @Test
    void getById_shouldReturnPayment() {
        Payment payment = new Payment();
        payment.setId(4L);

        when(paymentRepository.findById(4L)).thenReturn(Optional.of(payment));

        assertEquals(4L, paymentService.getById(4L).getId());
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(paymentRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> paymentService.getById(4L));
    }

    @Test
    void update_shouldApplyFieldsAndSave() {
        Payment existing = new Payment();
        existing.setId(9L);
        existing.setMethod("CASH");

        Payment updated = new Payment();
        updated.setMethod("CARD");
        updated.setPaidAt(LocalDateTime.now());

        when(paymentRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(existing)).thenReturn(existing);

        Payment result = paymentService.update(9L, updated);

        assertEquals("CARD", result.getMethod());
        assertEquals(updated.getPaidAt(), result.getPaidAt());
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(paymentRepository.existsById(6L)).thenReturn(true);

        paymentService.delete(6L);

        verify(paymentRepository).deleteById(6L);
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        when(paymentRepository.existsById(6L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> paymentService.delete(6L));
    }
}