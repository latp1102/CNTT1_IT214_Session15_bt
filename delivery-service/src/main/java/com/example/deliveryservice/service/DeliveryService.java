package com.example.deliveryservice.service;

import com.example.deliveryservice.event.InventoryUpdatedEvent;
import com.example.deliveryservice.event.LocationUpdatedEvent;
import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String DELIVERY_LOCATION_UPDATED_TOPIC = "delivery-location-updated";
    
    @Transactional
    public void createDelivery(Long orderId) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setCurrentLocation("WAREHOUSE");
        delivery.setStatus("PENDING");
        delivery.setCreatedAt(LocalDateTime.now());
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        
        LocationUpdatedEvent event = new LocationUpdatedEvent();
        event.setOrderId(orderId);
        event.setLocation("WAREHOUSE");
        event.setMessage("Delivery created and package is at warehouse");
        kafkaTemplate.send(DELIVERY_LOCATION_UPDATED_TOPIC, String.valueOf(orderId), event);
    }
    
    @Transactional
    public void updateLocation(Long orderId, String location) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId);
        if (delivery == null) {
            throw new RuntimeException("Delivery not found for order: " + orderId);
        }
        delivery.setCurrentLocation(location);
        delivery.setUpdatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        
        LocationUpdatedEvent event = new LocationUpdatedEvent();
        event.setOrderId(orderId);
        event.setLocation(location);
        event.setMessage("Order location updated to: " + location);
        kafkaTemplate.send(DELIVERY_LOCATION_UPDATED_TOPIC, String.valueOf(orderId), event);
    }
}