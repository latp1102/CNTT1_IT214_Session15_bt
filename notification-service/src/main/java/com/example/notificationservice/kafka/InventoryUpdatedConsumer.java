package com.example.notificationservice.kafka;

import com.example.notificationservice.event.InventoryUpdatedEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryUpdatedConsumer {
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "inventory-updated", groupId = "notification-service-group")
    public void consumeInventoryUpdated(InventoryUpdatedEvent event) {
        if (event.isSuccess()) {
            notificationService.sendPreparingNotification(event);
        }
    }
}
