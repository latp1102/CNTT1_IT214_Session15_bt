package com.example.order_service.kafka;

import com.example.order_service.event.PaymentResultEvent;
import com.example.order_service.model.enums.OrderStatus;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {
    private final OrderService orderService;
    
    @KafkaListener(topics = "payment-result", groupId = "order-service-group")
    public void consumePaymentResult(PaymentResultEvent event) {
        if (event.isSuccess()) {
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.SUCCESS);
        } else {
            orderService.updateOrderStatus(event.getOrderId(), OrderStatus.CANCELED);
        }
    }
}
