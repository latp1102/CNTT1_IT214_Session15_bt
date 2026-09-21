package com.example.order_service.controller;

import com.example.order_service.model.OrderDetail;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String userId, @RequestBody List<OrderDetail> details) {
        return new ResponseEntity<>(orderService.createOrder(userId, details), HttpStatus.CREATED);
    }
}
