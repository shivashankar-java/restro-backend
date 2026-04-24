package com.restro.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantRequest {

    private String name;
    private String address;
    private String phone;
    private String email;

    // menu IDs assigned to restaurant
    private List<Long> menuIds;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getAddress() {return address;}

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

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }
}
