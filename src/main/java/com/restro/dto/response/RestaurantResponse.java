package com.restro.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RestaurantResponse {

    private UUID id;

    private String name;

    private String address;

    private String phone;

    private String email;

    private List<String> menuNames;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public List<String> getMenuNames() {
        return menuNames;
    }

    public void setMenuNames(List<String> menuNames) {
        this.menuNames = menuNames;
    }
}
