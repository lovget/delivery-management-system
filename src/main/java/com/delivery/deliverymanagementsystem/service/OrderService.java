package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Payment;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.PaymentRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
        ProductRepository productRepository,
        PaymentRepository paymentRepository) {
            this.orderRepository = orderRepository;
            this.customerRepository = customerRepository;
            this.productRepository = productRepository;
            this.paymentRepository = paymentRepository;
        }

        public List<Order> getAll() {
            return orderRepository.findAll();
        }

        public Order getById(Long id) {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }

        @Transactional
        public Order createOrder(OrderCreateDto dto) {
            if (dto.getCustomerId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
            }
            if (dto.getProductIds() == null || dto.getProductIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productIds must contain at least one product");
            }
            if (dto.getStatus() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
            }

            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

            List<Product> products = new ArrayList<>();
            double totalAmount = 0;
            for (Long id : dto.getProductIds()) {
                Product product = productRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
                products.add(product);
                totalAmount += product.getPrice();
            }

            Order order = new Order();
            order.setCustomer(customer);
            order.setProducts(products);
            order.setStatus(dto.getStatus());
            order.setTotalAmount(totalAmount);

            Order savedOrder = orderRepository.save(order);

            if (dto.getPaymentMethod() != null && !dto.getPaymentMethod().isBlank()) {
                Payment payment = new Payment();
                payment.setOrder(savedOrder);
                payment.setMethod(dto.getPaymentMethod().trim());
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            return savedOrder;
        }

        @Transactional
        public Order createOrderWithError(OrderCreateDto dto) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

            Order order = new Order();
            order.setCustomer(customer);
            order.setStatus(dto.getStatus());

            orderRepository.save(order);

            throw new IllegalStateException("Rollback test");
        }

        @Transactional
        public Order updateStatus(Long id, OrderStatus status) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

            order.setStatus(status);
            return orderRepository.save(order);
        }

        @Transactional
        public void delete(Long id) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

            order.getProducts().clear();
            orderRepository.save(order);

            orderRepository.delete(order);
        }
}