package com.restro.service;

import java.util.UUID;

public interface InvoiceService {
    byte[] generateInvoice(UUID orderId);
}
