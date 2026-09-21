package com.example.notificationservice.kafka;

import com.example.notificationservice.event.PaymentResultEvent;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "payment-result", groupId = "notification-service-group")
    public void consumePaymentResult(PaymentResultEvent event) {
        notificationService.sendPaymentResultNotification(event);
    }
}
