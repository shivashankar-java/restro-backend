package com.restro.service;

public interface InvoiceService {
    byte[] generateInvoice(Long orderId);
}
