package com.example.paymentservice.service;

import com.example.paymentservice.event.PaymentResultEvent;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String PAYMENT_RESULT_TOPIC = "payment-result";
    
    @Transactional
    public PaymentResultEvent processPayment(Long orderId, Double amount, List<PaymentResultEvent.OrderItem> items) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PROCESSING");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        
        boolean success = true;
        String message = "Payment processed successfully";
        
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);
        
        PaymentResultEvent event = new PaymentResultEvent();
        event.setOrderId(orderId);
        event.setSuccess(success);
        event.setMessage(message);
        event.setItems(items);
        
        kafkaTemplate.send(PAYMENT_RESULT_TOPIC, String.valueOf(orderId), event);
        
        return event;
    }
}
