package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.*;
import com.bfs.restfulonlineshopping.service.OrderProductService;
import com.bfs.restfulonlineshopping.service.OrderService;
import com.bfs.restfulonlineshopping.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RabbitMqController {
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    private final OrderService orderService;
    private final OrderProductService orderProductService;

    @Autowired
    public RabbitMqController(RabbitTemplate rabbitTemplate, UserService userService, OrderService orderService, OrderProductService orderProductService) {
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
    }

    // create a scheduled task which will publish messages to the emailExchange at 6:00 PM everyday
    @Scheduled(cron = "0 0 18 * * ?", zone = "America/New_York")  // sec, min, hr, day-of-month, month, day-of-week, yr
    // @Scheduled(fixedRate = 15000)  // every 15s
    public void sendEmail() {
        // The content of the email is this user’s most recent Order history in detail

        // for each user, send corresponding email
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            List<Order> orders = orderService.getOrdersByUserId(user.getUserId());
            Order mostRecentOrder = orders.stream()
                    .max((a, b) -> a.getPlacementTime().compareTo(b.getPlacementTime()))
                    .orElse(null);

            // get the products in this order
            if (mostRecentOrder != null) {
                OrderProduct op = orderProductService.getOrderProductByOrderId(mostRecentOrder.getOrderId()).get();
                Product product = op.getProduct();

                // use DTO(Data Transfer Object) to display selected fields
                // 得有@ToString
                OrderDetails orderDetails = new OrderDetails(mostRecentOrder.getPlacementTime(), mostRecentOrder.getStatus(),
                        product.getDescription(), op.getExecutionRetailPrice(), op.getPurchasedQuantity(), user.getEmail());

                                // convertAndSend(String exchange, String routingKey, Object object)
                rabbitTemplate.convertAndSend("emailExchange", "bfs.test.email", orderDetails);
                // create了另外一个EmailApp 会listen to emailQueue然后send email
            }
        }
    }
}
