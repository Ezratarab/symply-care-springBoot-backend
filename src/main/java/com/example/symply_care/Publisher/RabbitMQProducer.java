package com.example.symply_care.Publisher;

import com.example.symply_care.entity.RabbitMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {
    @Value("${rabbitmq.exchnage.name}")
    private String exhange;
    @Value("${rabbitmq.queue.name}")
    private String queue;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);
    private RabbitTemplate rabbitTemplate;
    public RabbitMQProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }
    public void sendMessage(RabbitMQMessage rabbitMQMessage){
        LOGGER.info(String.format("Json Message sent -> %s", rabbitMQMessage));
        rabbitTemplate.convertAndSend(exhange,routingKey,rabbitMQMessage);
    }


}
