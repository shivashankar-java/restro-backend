package com.restro.service;

import com.restro.dto.request.PlaceOrderRequest;
import com.restro.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getMyOrders();

    String cancelOrder(Long orderId);

    String trackOrderStatus(Long orderId);

    String updateOrderStatus(Long orderId, String status);
}
