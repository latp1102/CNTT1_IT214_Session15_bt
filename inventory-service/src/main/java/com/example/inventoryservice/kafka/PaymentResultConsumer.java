package com.example.inventoryservice.kafka;

import com.example.inventoryservice.event.PaymentResultEvent;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {
    private final InventoryService inventoryService;
    
    @KafkaListener(topics = "payment-result", groupId = "inventory-service-group")
    public void consumePaymentResult(PaymentResultEvent event) {
        inventoryService.restoreStock(event);
    }
}
