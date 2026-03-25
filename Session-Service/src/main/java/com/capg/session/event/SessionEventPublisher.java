package com.capg.session.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionEventPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void publish(SessionEvent event) {
        rabbitTemplate.convertAndSend(
                "session.exchange",
                "session.key",
                event
        );
    }
}