package com.restro.service.impl;

import com.restro.dto.request.PlaceOrderRequest;
import com.restro.dto.response.OrderResponse;
import com.restro.entity.*;
import com.restro.mapper.OrderMapper;
import com.restro.repository.CartRepository;
import com.restro.repository.OrderRepository;
import com.restro.repository.PaymentRepository;
import com.restro.repository.UserRepository;
import com.restro.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(UserRepository userRepository, CartRepository cartRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, OrderMapper orderMapper) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderMapper = orderMapper;
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {


    Cart cart = cartRepository.findById(request.getCartId())
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }

    Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setRestaurant(cart.getRestaurant());
        order.setOrderItems(new ArrayList<>());
        order.setSubTotal(cart.getSubTotal());
        order.setDeliveryFee(cart.getDeliveryFee());
        order.setTaxAmount(cart.getTaxAmount());
        order.setDiscountAmount(cart.getDiscountAmount());
        order.setGrandTotal(cart.getGrandTotal());
        order.setDeliveryAddress(cart.getDeliveryAddress());
        order.setStatus(OrderStatus.PENDING);

    order = orderRepository.save(order);

        for (CartItem cartItem : cart.getCartItems()) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setMenuItem(cartItem.getMenuItem());
        item.setQuantity(cartItem.getQuantity());
        item.setPricePerUnit(cartItem.getPricePerUnit());
        item.setTotalPrice(cartItem.getTotalPrice());
        item.setSpecialInstructions(cartItem.getSpecialInstructions());

        order.getOrderItems().add(item);
    }

    order = orderRepository.save(order);

    Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getGrandTotal());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
        payment.setPaymentTime(LocalDateTime.now());

        paymentRepository.save(payment);

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return orderMapper.toOrderResponse(order);
     }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getMyOrders() {

        User user = getLoggedInUser();

        List<Order> orders = orderRepository
                .findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    public String cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!(order.getStatus() == OrderStatus.PENDING ||
                order.getStatus() == OrderStatus.CONFIRMED)) {
            throw new RuntimeException(
                    "Order cannot be cancelled after preparation starts"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return "Order cancelled successfully";
    }

    @Override
    public String trackOrderStatus(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return "Current Order Status: " + order.getStatus().name();
    }

    @Override
    public String updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);

        return "Order status updated to " + status;
    }
}