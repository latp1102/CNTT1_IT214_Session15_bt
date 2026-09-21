package com.example.paymentservice.controller;

import com.example.paymentservice.event.PaymentResultEvent;
import com.example.paymentservice.event.PaymentResultEvent.OrderItem;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestParam Long orderId, 
                                            @RequestParam Double amount,
                                            @RequestBody List<OrderItem> items) {
        PaymentResultEvent result = paymentService.processPayment(orderId, amount, items);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
