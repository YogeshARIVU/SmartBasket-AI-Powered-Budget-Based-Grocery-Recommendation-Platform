
package com.smartbasket.backend.shop.dto;

import java.time.LocalDateTime;

public class ShopResponse {

    private Long id;
    private String shopName;
    private String shopCode;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShopResponse() {
    }

    public ShopResponse(
            Long id,
            String shopName,
            String shopCode,
            String ownerName,
            String email,
            String phoneNumber,
            String address,
            String city,
            String state,
            String pincode,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.shopName = shopName;
        this.shopCode = shopCode;
        this.ownerName = ownerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
