package com.example.notificationservice.kafka;

import com.example.notificationservice.event.LocationUpdatedEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationUpdatedConsumer {
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "delivery-location-updated", groupId = "notification-service-group")
    public void consumeLocationUpdated(LocationUpdatedEvent event) {
        notificationService.sendLocationNotification(event);
    }
}
