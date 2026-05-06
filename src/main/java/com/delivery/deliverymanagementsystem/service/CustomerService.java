package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        if (customerRepository.existsByEmailIgnoreCase(customer.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with this email already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    public Customer update(Long id, Customer customer) {
        Customer existing = getById(id);

        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(customer.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with this email already exists");
        }

        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());

        return customerRepository.save(existing);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customerRepository.deleteById(id);
    }
}