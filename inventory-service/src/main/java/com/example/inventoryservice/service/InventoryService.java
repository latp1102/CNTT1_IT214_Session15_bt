package com.example.inventoryservice.service;

import com.example.inventoryservice.event.InventoryUpdatedEvent;
import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.event.PaymentResultEvent;
import com.example.inventoryservice.model.Stock;
import com.example.inventoryservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final StockRepository stockRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String INVENTORY_UPDATED_TOPIC = "inventory-updated";
    
    @Transactional
    public void processOrder(OrderCreatedEvent event) {
        boolean allSuccess = true;
        StringBuilder errorMessage = new StringBuilder();
        
        for (OrderCreatedEvent.OrderItem item : event.getItems()) {
            Stock stock = stockRepository.findByProductId(item.getProductId());
            if (stock == null) {
                allSuccess = false;
                errorMessage.append("Product ").append(item.getProductId()).append(" not found. ");
                continue;
            }
            
            if (stock.getTotal() < item.getQuantity()) {
                allSuccess = false;
                errorMessage.append("Insufficient stock for product ").append(item.getProductId()).append(". ");
            }
        }
        
        if (allSuccess) {
            for (OrderCreatedEvent.OrderItem item : event.getItems()) {
                Stock stock = stockRepository.findByProductId(item.getProductId());
                stock.setTotal(stock.getTotal() - item.getQuantity());
                stockRepository.save(stock);
            }
            InventoryUpdatedEvent updateEvent = new InventoryUpdatedEvent(event.getOrderId(), true, "Inventory updated successfully");
            kafkaTemplate.send(INVENTORY_UPDATED_TOPIC, String.valueOf(event.getOrderId()), updateEvent);
        } else {
            InventoryUpdatedEvent updateEvent = new InventoryUpdatedEvent(event.getOrderId(), false, errorMessage.toString());
            kafkaTemplate.send(INVENTORY_UPDATED_TOPIC, String.valueOf(event.getOrderId()), updateEvent);
        }
    }
    
    @Transactional
    public void restoreStock(PaymentResultEvent event) {
        if (!event.isSuccess()) {
            for (PaymentResultEvent.OrderItem item : event.getItems()) {
                Stock stock = stockRepository.findByProductId(item.getProductId());
                if (stock != null) {
                    stock.setTotal(stock.getTotal() + item.getQuantity());
                    stockRepository.save(stock);
                }
            }
        }
    }
}
