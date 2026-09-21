package com.example.deliveryservice.kafka;

import com.example.deliveryservice.event.InventoryUpdatedEvent;
import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryUpdatedConsumer {
    private final DeliveryService deliveryService;
    
    @KafkaListener(topics = "inventory-updated", groupId = "delivery-service-group")
    public void consumeInventoryUpdated(InventoryUpdatedEvent event) {
        if (event.isSuccess()) {
            deliveryService.createDelivery(event.getOrderId());
        }
    }
}