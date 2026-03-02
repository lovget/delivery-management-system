package com.delivery.deliverymanagementsystem.repository;

import com.delivery.deliverymanagementsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}