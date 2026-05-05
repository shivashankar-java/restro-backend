package com.restro.controller;

import com.restro.dto.request.PlaceOrderRequest;
import com.restro.dto.response.OrderResponse;
import com.restro.service.InvoiceService;
import com.restro.service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public OrderController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody
                                                        PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    // CUSTOMER → Single order details
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // CUSTOMER → My Orders
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    // CUSTOMER → Cancel Order
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    // CUSTOMER → Track Order
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/track/{orderId}")
    public ResponseEntity<String> trackOrderStatus(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.trackOrderStatus(orderId));
    }

    // ADMIN → Update Order Status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderId, status)
        );
    }

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {

        try {
            byte[] pdf = invoiceService.generateInvoice(orderId);

            String fileName = "invoice_" + orderId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + fileName)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header("X-Message", "Invoice downloaded successfully") //  custom message
                    .body(pdf);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Failed to generate invoice")
                    .body(null);
        }
    }
}

