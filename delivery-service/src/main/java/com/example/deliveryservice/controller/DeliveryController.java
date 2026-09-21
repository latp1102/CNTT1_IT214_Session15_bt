package com.example.deliveryservice.controller;

import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    
    @PostMapping("/{orderId}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long orderId, @RequestParam String location) {
        deliveryService.updateLocation(orderId, location);
        return new ResponseEntity<>("Location updated to: " + location, HttpStatus.OK);
    }
}