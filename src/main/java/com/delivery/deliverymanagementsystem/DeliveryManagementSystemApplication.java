package com.delivery.deliverymanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeliveryManagementSystemApplication {

    public static void main(String[] args) {
        configureDatasourceUrlForPaaS();
        SpringApplication.run(DeliveryManagementSystemApplication.class, args);
    }

    private static void configureDatasourceUrlForPaaS() {
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            datasourceUrl = System.getenv("DB_URL");
        }

        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            return;
        }

        if (datasourceUrl.startsWith("postgres://") || datasourceUrl.startsWith("postgresql://")) {
            datasourceUrl = "jdbc:" + datasourceUrl;
        }

        System.setProperty("spring.datasource.url", datasourceUrl);
    }
}