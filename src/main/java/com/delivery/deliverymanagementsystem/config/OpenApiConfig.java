package com.delivery.deliverymanagementsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Management System API")
                        .description("API для управления доставкой: клиенты, товары, категории, заказы и платежи")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Delivery Management Team")
                                .email("support@delivery.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}