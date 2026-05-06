package com.delivery.deliverymanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для создания/обновления клиента")
public class CustomerCreateDto {

    @Schema(description = "Имя клиента", example = "Ivan Ivanov")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Schema(description = "Email клиента", example = "ivan@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Schema(description = "Телефон клиента", example = "+79990001122")
    @NotBlank(message = "Phone must not be blank")
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}