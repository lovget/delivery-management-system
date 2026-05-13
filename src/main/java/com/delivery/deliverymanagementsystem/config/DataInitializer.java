package com.delivery.deliverymanagementsystem.config;

import com.delivery.deliverymanagementsystem.entity.Category;
import com.delivery.deliverymanagementsystem.entity.Customer;
import com.delivery.deliverymanagementsystem.entity.Order;
import com.delivery.deliverymanagementsystem.entity.OrderStatus;
import com.delivery.deliverymanagementsystem.entity.Product;
import com.delivery.deliverymanagementsystem.repository.CategoryRepository;
import com.delivery.deliverymanagementsystem.repository.CustomerRepository;
import com.delivery.deliverymanagementsystem.repository.OrderRepository;
import com.delivery.deliverymanagementsystem.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           CustomerRepository customerRepository,
                           OrderRepository orderRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            seedCategories();
        }
        if (productRepository.count() == 0) {
            seedProducts();
        }
        if (customerRepository.count() == 0) {
            seedCustomers();
        }
        if (orderRepository.count() == 0) {
            seedOrders();
        }
    }

    private void seedCategories() {
        List.of("Пицца", "Суши", "Бургеры", "Напитки", "Десерты")
                .forEach(name -> {
                    Category category = new Category();
                    category.setName(name);
                    categoryRepository.save(category);
                });
    }

    private void seedProducts() {
        Map<String, Double> data = Map.of(
                "Пепперони", 22.50,
                "Маргарита", 19.90,
                "Дракон ролл", 28.40,
                "Филадельфия", 31.00,
                "Двойной бургер", 24.80,
                "Картофель фри", 8.90,
                "Кола 0.5", 4.50,
                "Чизкейк", 11.20,
                "Морс клюквенный", 5.40
        );

        data.forEach((name, price) -> {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setCategories(resolveCategoriesForProduct(name));
            productRepository.save(product);
        });
    }

    private Set<Category> resolveCategoriesForProduct(String productName) {
        Set<String> names = switch (productName) {
            case "Пепперони", "Маргарита" -> Set.of("Пицца");
            case "Дракон ролл", "Филадельфия" -> Set.of("Суши");
            case "Двойной бургер", "Картофель фри" -> Set.of("Бургеры");
            case "Кола 0.5", "Морс клюквенный" -> Set.of("Напитки");
            case "Чизкейк" -> Set.of("Десерты");
            default -> Set.of();
        };

        Set<Category> categories = new HashSet<>();
        for (String name : names) {
            categoryRepository.findByName(name).ifPresent(categories::add);
        }
        return categories;
    }

    private void seedCustomers() {
        List<Customer> customers = List.of(
                customer("Артём Савицкий", "savitsky@delivery.by", "+375291112233"),
                customer("Владислав Ковалёв", "kovalev@food.by", "+375447778899"),
                customer("Егор Дрозд", "drozd@yam.by", "+375336661122"),
                customer("Алина Жукова", "alina.zh@tut.by", "+375259990011"),
                customer("Дарья Мельник", "d.melnik@inbox.by", "+375297771144")
        );
        customerRepository.saveAll(customers);
    }

    private Customer customer(String name, String email, String phone) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        return customer;
    }

    private void seedOrders() {
        List<Customer> customers = customerRepository.findAll();
        List<Product> products = productRepository.findAll();
        if (customers.isEmpty() || products.isEmpty()) {
            return;
        }

        orderRepository.save(order(customers.get(0), Set.of(findProduct(products, "Пепперони"), findProduct(products, "Кола 0.5")), OrderStatus.NEW));
        orderRepository.save(order(customers.get(1), Set.of(findProduct(products, "Филадельфия"), findProduct(products, "Морс клюквенный")), OrderStatus.COOKING));
        orderRepository.save(order(customers.get(2), Set.of(findProduct(products, "Двойной бургер"), findProduct(products, "Картофель фри")), OrderStatus.DELIVERING));
        orderRepository.save(order(customers.get(3), Set.of(findProduct(products, "Маргарита"), findProduct(products, "Чизкейк")), OrderStatus.DONE));
    }

    private Product findProduct(List<Product> products, String name) {
        return products.stream().filter(product -> product.getName().equals(name)).findFirst().orElseThrow();
    }

    private Order order(Customer customer, Set<Product> products, OrderStatus status) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setStatus(status);
        order.setTotalAmount(products.stream().mapToDouble(Product::getPrice).sum());
        return order;
    }
}