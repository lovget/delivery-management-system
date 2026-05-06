package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import com.delivery.deliverymanagementsystem.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldCalculateTotalAndDefaultStatus() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setCustomerId(1L);
        dto.setProductIds(List.of(10L, 20L));

        Customer customer = new Customer();
        customer.setId(1L);

        Product first = new Product();
        first.setId(10L);
        first.setPrice(100.0);

        Product second = new Product();
        second.setId(20L);
        second.setPrice(40.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(first));
        when(productRepository.findById(20L)).thenReturn(Optional.of(second));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(dto);

        assertEquals(140.0, result.getTotalAmount());
        assertEquals(OrderStatus.NEW, result.getStatus());
        assertEquals(2, result.getProducts().size());
    }

    @Test
    void createOrdersBulkNonTransactional_shouldKeepFirstSaveWhenSecondFails() {
        OrderCreateDto firstDto = new OrderCreateDto();
        firstDto.setCustomerId(1L);
        firstDto.setProductIds(List.of(10L));

        OrderCreateDto secondDto = new OrderCreateDto();
        secondDto.setCustomerId(1L);
        secondDto.setProductIds(List.of(999L));

        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setPrice(50.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkNonTransactional(List.of(firstDto, secondDto)));

        verify(orderRepository, times(1)).save(any(Order.class));
    }


    @Test
    void createOrdersBulkTransactional_shouldFailWhenListIsEmpty() {
        assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkTransactional(List.of()));
    }

    @Test
    void createOrdersBulkTransactional_shouldSaveAllWhenInputIsValid() {
        OrderCreateDto firstDto = new OrderCreateDto();
        firstDto.setCustomerId(1L);
        firstDto.setProductIds(List.of(10L));

        OrderCreateDto secondDto = new OrderCreateDto();
        secondDto.setCustomerId(1L);
        secondDto.setProductIds(List.of(20L));

        Customer customer = new Customer();
        customer.setId(1L);

        Product first = new Product();
        first.setId(10L);
        first.setPrice(10.0);

        Product second = new Product();
        second.setId(20L);
        second.setPrice(20.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(first));
        when(productRepository.findById(20L)).thenReturn(Optional.of(second));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Order> result = orderService.createOrdersBulkTransactional(List.of(firstDto, secondDto));

        assertEquals(2, result.size());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(2)).save(captor.capture());
        assertEquals(10.0, captor.getAllValues().get(0).getTotalAmount());
        assertEquals(20.0, captor.getAllValues().get(1).getTotalAmount());
    }
}