package com.example.notificationservice.service;

import com.example.notificationservice.event.InventoryUpdatedEvent;
import com.example.notificationservice.event.LocationUpdatedEvent;
import com.example.notificationservice.event.OrderCreatedEvent;
import com.example.notificationservice.event.PaymentResultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    public void sendPendingNotification(OrderCreatedEvent event) {
        log.info("Sending PENDING notification to user {} for order {}: Order is pending", 
            event.getUserId(), event.getOrderId());
    }
    
    public void sendPreparingNotification(InventoryUpdatedEvent event) {
        log.info("Sending PREPARING notification for order {}: {}", 
            event.getOrderId(), event.getMessage());
    }
    
    public void sendLocationNotification(LocationUpdatedEvent event) {
        log.info("Sending location notification for order {}: {} - {}", 
            event.getOrderId(), event.getLocation(), event.getMessage());
    }
    
    public void sendPaymentResultNotification(PaymentResultEvent event) {
        String status = event.isSuccess() ? "SUCCESS" : "CANCELED";
        log.info("Sending {} notification for order {}: {}", 
            status, event.getOrderId(), event.getMessage());
    }
}
