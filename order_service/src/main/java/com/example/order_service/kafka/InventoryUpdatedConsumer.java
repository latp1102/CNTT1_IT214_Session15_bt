package com.example.order_service.kafka;

import com.example.order_service.event.InventoryUpdatedEvent;
import com.example.order_service.model.enums.OrderStatus;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryUpdatedConsumer {
    private final OrderService orderService;
    
    @KafkaListener(topics = "inventory-updated", groupId = "order-service-group")
    public void consumeInventoryUpdated(InventoryUpdatedEvent event) {
        if (event.isSuccess()) {
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.PREPARING);
        } else {
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.FAILED);
        }
    }
}
