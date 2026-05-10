package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void create_shouldSaveWhenEmailIsUnique() {
        Customer customer = new Customer();
        customer.setEmail("a@a.com");

        when(customerRepository.existsByEmailIgnoreCase("a@a.com")).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.create(customer);

        assertEquals("a@a.com", result.getEmail());
        verify(customerRepository).save(customer);
    }

    @Test
    void create_shouldThrowWhenEmailExists() {
        Customer customer = new Customer();
        customer.setEmail("a@a.com");

        when(customerRepository.existsByEmailIgnoreCase("a@a.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> customerService.create(customer));
    }

    @Test
    void getAll_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer()));

        assertEquals(1, customerService.getAll().size());
    }

    @Test
    void getById_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setId(11L);

        when(customerRepository.findById(11L)).thenReturn(Optional.of(customer));

        assertEquals(11L, customerService.getById(11L).getId());
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(customerRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> customerService.getById(11L));
    }

    @Test
    void update_shouldUpdateAndSaveWhenEmailIsUnique() {
        Customer existing = new Customer();
        existing.setId(5L);
        existing.setName("Old");
        existing.setEmail("old@mail.com");
        existing.setPhone("1");

        Customer incoming = new Customer();
        incoming.setName("New");
        incoming.setEmail("new@mail.com");
        incoming.setPhone("2");

        when(customerRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmailIgnoreCaseAndIdNot("new@mail.com", 5L)).thenReturn(false);
        when(customerRepository.save(existing)).thenReturn(existing);

        Customer result = customerService.update(5L, incoming);

        assertEquals("New", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("2", result.getPhone());
    }

    @Test
    void update_shouldThrowWhenEmailExists() {
        Customer existing = new Customer();
        existing.setId(5L);
        existing.setEmail("old@mail.com");

        Customer incoming = new Customer();
        incoming.setEmail("dup@mail.com");

        when(customerRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmailIgnoreCaseAndIdNot("dup@mail.com", 5L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> customerService.update(5L, incoming));
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(customerRepository.existsById(7L)).thenReturn(true);

        customerService.delete(7L);

        verify(customerRepository).deleteById(7L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(customerRepository.existsById(7L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> customerService.delete(7L));
    }
}