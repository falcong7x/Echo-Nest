package com.econest.model.operations;
public record OrderSummary(int orderId, int clientId, String clientName, String email, String orderDate,
                           String status, double total) { }
