package com.amirul.tabernBackend.model;

public enum OrderStatus {
    PENDING,            // Order placed but not processed
    PROCESSING,         // Order is being processed
    CONFIRMED,          // Order is confirmed
    SHIPPED,            // Order has been shipped
    OUT_FOR_DELIVERY,   // Order is out for delivery
    DELIVERED,          // Order has been delivered
    CANCELLED,          // Order was cancelled
    REFUNDED,           // Order was refunded
    FAILED              // Payment failed or order processing failed
}
