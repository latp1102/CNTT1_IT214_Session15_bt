package com.example.notificationservice.kafka;

import com.example.notificationservice.event.OrderCreatedEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        notificationService.sendPendingNotification(event);
    }
}
