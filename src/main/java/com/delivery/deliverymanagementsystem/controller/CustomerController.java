package com.delivery.deliverymanagementsystem.controller;

import com.delivery.deliverymanagementsystem.dto.CustomerCreateDto;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Операции с клиентами")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Создать клиента")
    public Customer create(@Valid @RequestBody CustomerCreateDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        return customerService.create(customer);
    }

    @GetMapping
    @Operation(summary = "Получить список клиентов")
    public List<Customer> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента по id")
    public Customer getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить клиента")
    public Customer update(@PathVariable Long id, @Valid @RequestBody CustomerCreateDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        return customerService.update(id, customer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}