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

    // menu IDs assigned to restaurant
    private List<UUID> menuIds;

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

    public List<UUID> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<UUID> menuIds) {
        this.menuIds = menuIds;
    }
}
