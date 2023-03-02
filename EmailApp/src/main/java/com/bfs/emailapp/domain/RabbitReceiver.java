package com.bfs.emailapp.domain;

import com.bfs.emailapp.entity.OrderDetails;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RabbitReceiver {
    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);

    @Autowired
    public RabbitReceiver(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @RabbitListener(queues = "emailQueue")
    public void receivedMessage(OrderDetails orderDetails) {
        logger.info("Most Recent Order Details Received: " + orderDetails);
        sendEmail(orderDetails);
    }

    public void sendEmail(OrderDetails orderDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yztest1117@gmail.com");
        message.setTo(orderDetails.getUserEmail());
        message.setSubject("Your Most Recent Order Details");
        message.setText(orderDetails.toString());
        emailSender.send(message);
    }
}
