package com.restro.service;

import com.restro.dto.request.PlaceOrderRequest;
import com.restro.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse getOrderById(UUID orderId);

    List<OrderResponse> getMyOrders();

    String cancelOrder(UUID orderId);

    String trackOrderStatus(UUID orderId);

    String updateOrderStatus(UUID orderId, String status);
}
