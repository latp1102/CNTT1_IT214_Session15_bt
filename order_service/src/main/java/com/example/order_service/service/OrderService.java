package com.example.order_service.service;

import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderDetail;
import com.example.order_service.model.enums.OrderStatus;
import com.example.order_service.repository.OrderDetailRepository;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String ORDER_CREATED_TOPIC = "order-created";
    
    @Transactional
    public Order createOrder(String userId, List<OrderDetail> details) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        
        double total = details.stream()
            .mapToDouble(d -> d.getPrice() * d.getQuantity())
            .sum();
        order.setTotal(total);
        
        order = orderRepository.save(order);
        
        for (OrderDetail detail : details) {
            detail.setOrder(order);
            orderDetailRepository.save(detail);
        }
        
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setUserId(userId);
        List<OrderCreatedEvent.OrderItem> items = new ArrayList<>();
        for (OrderDetail detail : details) {
            items.add(new OrderCreatedEvent.OrderItem(detail.getProductId(), detail.getQuantity(), detail.getPrice()));
        }
        event.setItems(items);
        
        kafkaTemplate.send(ORDER_CREATED_TOPIC, String.valueOf(order.getId()), event);
        
        return order;
    }
    
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
