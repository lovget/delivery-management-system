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

    private static final String CATEGORY_PIZZA = "Пицца";
    private static final String CATEGORY_SUSHI = "Суши";
    private static final String CATEGORY_BURGERS = "Бургеры";
    private static final String CATEGORY_DRINKS = "Напитки";
    private static final String CATEGORY_DESSERTS = "Десерты";

    private static final String PRODUCT_PEPPERONI = "Пепперони";
    private static final String PRODUCT_MARGHERITA = "Маргарита";
    private static final String PRODUCT_DRAGON_ROLL = "Дракон ролл";
    private static final String PRODUCT_PHILADELPHIA = "Филадельфия";
    private static final String PRODUCT_DOUBLE_BURGER = "Двойной бургер";
    private static final String PRODUCT_FRIES = "Картофель фри";
    private static final String PRODUCT_COLA = "Кола 0.5";
    private static final String PRODUCT_CHEESECAKE = "Чизкейк";
    private static final String PRODUCT_CRANBERRY_DRINK = "Морс клюквенный";

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
        List.of(CATEGORY_PIZZA, CATEGORY_SUSHI, CATEGORY_BURGERS, CATEGORY_DRINKS, CATEGORY_DESSERTS)
                .forEach(name -> {
                    Category category = new Category();
                    category.setName(name);
                    categoryRepository.save(category);
                });
    }

    private void seedProducts() {
        Map<String, Double> data = Map.of(
                PRODUCT_PEPPERONI, 22.50,
                PRODUCT_MARGHERITA, 19.90,
                PRODUCT_DRAGON_ROLL, 28.40,
                PRODUCT_PHILADELPHIA, 31.00,
                PRODUCT_DOUBLE_BURGER, 24.80,
                PRODUCT_FRIES, 8.90,
                PRODUCT_COLA, 4.50,
                PRODUCT_CHEESECAKE, 11.20,
                PRODUCT_CRANBERRY_DRINK, 5.40
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
            case PRODUCT_PEPPERONI, PRODUCT_MARGHERITA -> Set.of(CATEGORY_PIZZA);
            case PRODUCT_DRAGON_ROLL, PRODUCT_PHILADELPHIA -> Set.of(CATEGORY_SUSHI);
            case PRODUCT_DOUBLE_BURGER, PRODUCT_FRIES -> Set.of(CATEGORY_BURGERS);
            case PRODUCT_COLA, PRODUCT_CRANBERRY_DRINK -> Set.of(CATEGORY_DRINKS);
            case PRODUCT_CHEESECAKE -> Set.of(CATEGORY_DESSERTS);
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

        orderRepository.save(order(customers.get(0), Set.of(findProduct(products, PRODUCT_PEPPERONI), findProduct(products, PRODUCT_COLA)), OrderStatus.NEW));
        orderRepository.save(order(customers.get(1), Set.of(findProduct(products, PRODUCT_PHILADELPHIA), findProduct(products, PRODUCT_CRANBERRY_DRINK)), OrderStatus.COOKING));
        orderRepository.save(order(customers.get(2), Set.of(findProduct(products, PRODUCT_DOUBLE_BURGER), findProduct(products, PRODUCT_FRIES)), OrderStatus.DELIVERING));
        orderRepository.save(order(customers.get(3), Set.of(findProduct(products, PRODUCT_MARGHERITA), findProduct(products, PRODUCT_CHEESECAKE)), OrderStatus.DONE));
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
