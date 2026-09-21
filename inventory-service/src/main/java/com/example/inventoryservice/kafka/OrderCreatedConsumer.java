package com.example.inventoryservice.kafka;

import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final InventoryService inventoryService;
    
    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        inventoryService.processOrder(event);
    }
}
