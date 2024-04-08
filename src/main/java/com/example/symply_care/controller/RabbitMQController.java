package com.example.symply_care.controller;

import com.example.symply_care.Publisher.RabbitMQProducer;
import com.example.symply_care.entity.RabbitMQMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQController {

    @Autowired
    private RabbitMQProducer producer;


    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestBody RabbitMQMessage message) {
        try {
            producer.sendMessage(message);
            return ResponseEntity.ok("Message sent to RabbitMQ.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send message to RabbitMQ: " + e.getMessage());
        }
    }
}
