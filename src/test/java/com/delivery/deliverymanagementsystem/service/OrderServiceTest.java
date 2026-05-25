package com.delivery.deliverymanagementsystem.service;

import com.delivery.deliverymanagementsystem.dto.OrderCreateDto;
import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Sort ORDER_QUEUE_SORT = Sort.by(
            Sort.Order.asc("createdAt").nullsFirst(),
            Sort.Order.asc("id")
    );

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product firstProduct;
    private Product secondProduct;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");
        customer.setEmail("alice@mail.com");
        customer.setPhone("123");

        firstProduct = new Product();
        firstProduct.setId(10L);
        firstProduct.setName("Burger");
        firstProduct.setPrice(100.0);

        secondProduct = new Product();
        secondProduct.setId(20L);
        secondProduct.setName("Fries");
        secondProduct.setPrice(50.0);
    }

    @Test
    void getAll_returnsRepositoryResult() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll(ORDER_QUEUE_SORT)).thenReturn(orders);

        assertSame(orders, orderService.getAll());
    }

    @Test
    void getById_returnsOrderWhenExists() {
        Order order = new Order();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        assertSame(order, orderService.getById(5L));
    }

    @Test
    void getById_throwsWhenMissing() {
        when(orderRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> orderService.getById(5L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getFiltered_usesCacheOnSecondCall() {
        Order order = new Order();
        List<Order> expected = List.of(order);
        when(orderRepository.findByStatusAndAmount(OrderStatus.NEW, 100.0)).thenReturn(expected);

        List<Order> first = orderService.getFiltered(OrderStatus.NEW, 100.0);
        List<Order> second = orderService.getFiltered(OrderStatus.NEW, 100.0);

        assertSame(expected, first);
        assertSame(expected, second);
        verify(orderRepository, times(1)).findByStatusAndAmount(OrderStatus.NEW, 100.0);
    }

    @Test
    void getFilteredNative_mapsRowsAndUsesCache() {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> rows = List.of(
                new Object[]{1L, "NEW", 150.0, now, 2L, "Bob", "bob@mail.com", "456", 10L, "Burger", 100.0, 1000L, "FastFood"},
                new Object[]{1L, "NEW", 150.0, now, 2L, "Bob", "bob@mail.com", "456", 20L, "Fries", 50.0, null, null},
                new Object[]{1L, "NEW", 150.0, now, 2L, "Bob", "bob@mail.com", "456", 10L, "Burger", 100.0, 1000L, "FastFood"}
        );
        when(orderRepository.findByStatusAndAmountNativeRaw("NEW", 100.0)).thenReturn(rows);

        List<Order> result = orderService.getFilteredNative(OrderStatus.NEW, 100.0);
        List<Order> cached = orderService.getFilteredNative(OrderStatus.NEW, 100.0);

        assertEquals(1, result.size());
        Order mapped = result.get(0);
        assertEquals(1L, mapped.getId());
        assertEquals(now, mapped.getCreatedAt());
        assertEquals(2, mapped.getProducts().size());

        Product burger = mapped.getProducts().stream().filter(p -> p.getId().equals(10L)).findFirst().orElseThrow();
        assertEquals(1, burger.getCategories().size());
        Category category = burger.getCategories().iterator().next();
        assertEquals(1000L, category.getId());

        assertSame(result, cached);
        verify(orderRepository, times(1)).findByStatusAndAmountNativeRaw("NEW", 100.0);
    }

    @Test
    void getByCustomerName_usesCache() {
        List<Order> orders = List.of(new Order());
        when(orderRepository.findByCustomerNameAndAmount("Alice", 50.0)).thenReturn(orders);

        orderService.getByCustomerName("Alice", 50.0);
        orderService.getByCustomerName("Alice", 50.0);

        verify(orderRepository, times(1)).findByCustomerNameAndAmount("Alice", 50.0);
    }

    @Test
    void getByCustomerNameNative_mapsRowsAndCaches() {
        Object[] row = new Object[]{
                2L, "PROCESSING", 50.0, LocalDateTime.now(), 1L, "Alice", "alice@mail.com", "123",
                20L, "Fries", 50.0, null, null
        };
        List<Object[]> rows = java.util.Collections.singletonList(row);
        when(orderRepository.findByCustomerNameAndAmountNativeRaw("Alice", 40.0)).thenReturn(rows);

        List<Order> first = orderService.getByCustomerNameNative("Alice", 40.0);
        List<Order> second = orderService.getByCustomerNameNative("Alice", 40.0);

        assertEquals(1, first.size());
        assertSame(first, second);
        verify(orderRepository, times(1)).findByCustomerNameAndAmountNativeRaw("Alice", 40.0);
    }

    @Test
    void getPaged_delegatesToRepository() {
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findAll(PageRequest.of(1, 5, ORDER_QUEUE_SORT))).thenReturn(page);

        Page<Order> result = orderService.getPaged(1, 5);

        assertSame(page, result);
    }

    @Test
    void createOrder_buildsOrderAndClearsCache() {
        OrderCreateDto dto = validDto(List.of(10L, 20L), null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(20L)).thenReturn(Optional.of(secondProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findByStatusAndAmount(OrderStatus.NEW, 1.0)).thenReturn(List.of(new Order()));

        orderService.getFiltered(OrderStatus.NEW, 1.0);
        Order created = orderService.createOrder(dto);
        orderService.getFiltered(OrderStatus.NEW, 1.0);

        assertEquals(150.0, created.getTotalAmount());
        assertEquals(OrderStatus.NEW, created.getStatus());
        verify(orderRepository, times(2)).findByStatusAndAmount(OrderStatus.NEW, 1.0);
    }

    @Test
    void bulkTransactional_throwsOnNullList() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkTransactional(null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void bulkNonTransactional_savesOnlyFirstWhenSecondInvalid() {
        OrderCreateDto firstDto = validDto(List.of(10L), OrderStatus.NEW);
        OrderCreateDto secondDto = validDto(List.of(999L), OrderStatus.NEW);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<OrderCreateDto> orders = List.of(firstDto, secondDto);

        assertThrows(ResponseStatusException.class,
                () -> orderService.createOrdersBulkNonTransactional(orders));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void bulkTransactional_savesAllForValidInput() {
        OrderCreateDto firstDto = validDto(List.of(10L), OrderStatus.PROCESSING);
        OrderCreateDto secondDto = validDto(List.of(20L), OrderStatus.DONE);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(20L)).thenReturn(Optional.of(secondProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Order> result = orderService.createOrdersBulkTransactional(List.of(firstDto, secondDto));

        assertEquals(2, result.size());
        assertEquals(OrderStatus.PROCESSING, result.get(0).getStatus());
        assertEquals(OrderStatus.DONE, result.get(1).getStatus());
    }

    @Test
    void updateStatus_updatesAndClearsCache() {
        Order order = new Order();
        order.setId(7L);
        order.setStatus(OrderStatus.NEW);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order updated = orderService.updateStatus(7L, OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, updated.getStatus());
    }

    @Test
    void updateStatus_throwsWhenMissing() {
        when(orderRepository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> orderService.updateStatus(7L, OrderStatus.PROCESSING));
    }

    @Test
    void delete_throwsWhenMissing() {
        when(orderRepository.existsById(4L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> orderService.delete(4L));
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void delete_deletesWhenExists() {
        when(orderRepository.existsById(4L)).thenReturn(true);

        orderService.delete(4L);

        verify(orderRepository).deleteById(4L);
    }

    @Test
    void createOrder_throwsWhenCustomerMissing() {
        OrderCreateDto dto = validDto(List.of(10L), OrderStatus.NEW);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(dto));
    }

    @Test
    void createOrder_throwsWhenProductIdsEmpty() {
        OrderCreateDto dto = validDto(List.of(), OrderStatus.NEW);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(dto));
    }

    @Test
    void createOrder_throwsWhenProductMissing() {
        OrderCreateDto dto = validDto(List.of(10L), OrderStatus.NEW);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(dto));
    }

    @Test
    void createOrder_removesDuplicateProductsBySet() {
        OrderCreateDto dto = validDto(List.of(10L, 10L), OrderStatus.NEW);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(firstProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        assertNotNull(order.getProducts());
        assertEquals(1, order.getProducts().size());
    }

    @Test
    void createOrder_usesExplicitStatusWhenProvided() {
        OrderCreateDto dto = validDto(List.of(10L), OrderStatus.PROCESSING);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(firstProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(dto);

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    private OrderCreateDto validDto(List<Long> productIds, OrderStatus status) {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setCustomerId(1L);
        dto.setProductIds(productIds);
        dto.setStatus(status);
        return dto;
    }
}
