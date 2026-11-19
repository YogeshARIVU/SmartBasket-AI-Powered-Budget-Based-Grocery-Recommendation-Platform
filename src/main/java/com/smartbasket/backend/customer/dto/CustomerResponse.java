
package com.smartbasket.backend.customer.dto;

import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerResponse() {
    }

    public CustomerResponse(
            Long id,
            String fullName,
            String email,
            String phoneNumber,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
