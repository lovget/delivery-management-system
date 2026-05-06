package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long customerId;
    private Long validProductId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = new Customer();
        customer.setName("Integration User");
        customer.setEmail("integration@example.com");
        customer.setPhone("+10000000000");
        customerId = customerRepository.save(customer).getId();

        Product product = new Product();
        product.setName("Integration Product");
        product.setPrice(99.0);
        validProductId = productRepository.save(product).getId();
    }

    @Test
    void transactionalBulk_shouldRollbackAllWhenOneItemFails() {
        OrderCreateDto first = new OrderCreateDto();
        first.setCustomerId(customerId);
        first.setProductIds(List.of(validProductId));

        OrderCreateDto second = new OrderCreateDto();
        second.setCustomerId(customerId);
        second.setProductIds(List.of(999_999L));

        long before = orderRepository.count();

        assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkTransactional(List.of(first, second)));

        assertEquals(before, orderRepository.count());
    }

    @Test
    void nonTransactionalBulk_shouldPersistSuccessfulItemsBeforeFailure() {
        OrderCreateDto first = new OrderCreateDto();
        first.setCustomerId(customerId);
        first.setProductIds(List.of(validProductId));

        OrderCreateDto second = new OrderCreateDto();
        second.setCustomerId(customerId);
        second.setProductIds(List.of(999_999L));

        long before = orderRepository.count();

        assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkNonTransactional(List.of(first, second)));

        assertEquals(before + 1, orderRepository.count());
        Order savedOrder = orderRepository.findAll().get(0);
        assertEquals(99.0, savedOrder.getTotalAmount());
    }
}