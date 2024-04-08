package com.example.symply_care.Consumer;

import com.example.symply_care.entity.RabbitMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(RabbitMQMessage rabbitMQMessage){
        LOGGER.info(String.format("Recieved message -> %s", rabbitMQMessage.toString()));
    }
}
