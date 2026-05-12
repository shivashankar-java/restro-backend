package com.restro.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RestaurantRequest {

    private String name;
    private String address;
    private String phone;
    private String email;

    private UUID categoryId;

    // Multiple menus
    private List<MenuRequest> menus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<MenuRequest> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuRequest> menus) {
        this.menus = menus;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
