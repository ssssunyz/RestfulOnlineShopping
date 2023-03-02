package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.OrderProduct;
import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import com.bfs.restfulonlineshopping.entity.response.*;
import com.bfs.restfulonlineshopping.exception.NotAuthorizedException;
import com.bfs.restfulonlineshopping.exception.OrderNotFoundException;
import com.bfs.restfulonlineshopping.service.OrderProductService;
import com.bfs.restfulonlineshopping.service.OrderService;
import com.bfs.restfulonlineshopping.service.ProductService;
import com.bfs.restfulonlineshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// this class is made for the Asynchronous homework
@RestController
@RequestMapping("/OnlineShop/async")
public class AsyncController {
    private UserService userService;
    private ProductService productService;
    private OrderService orderService;
    private OrderProductService orderProductService;

    @Autowired
    public AsyncController(UserService userService, ProductService productService, OrderService orderService, OrderProductService orderProductService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
    }

    @GetMapping("/user/{userId}/order/{orderId}")
    public CompletableFuture<UserOrderResponse> getUserOrder(@PathVariable int userId, @PathVariable String orderId) {
        System.out.println("async here");

        // check valid orderId
        UUID orderUUID;
        try {
            orderUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(orderId);
        }

        // Each service method call will be handled by an individual thread.
        CompletableFuture<Optional<Order>> orderOptionalFuture = orderService.getOrderByIdAsync(orderUUID);
        CompletableFuture<Optional<OrderProduct>> orderProductFuture = orderProductService.getOrderProductByOrderIdAsync(orderUUID);

        // wait for the above two futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(orderOptionalFuture, orderProductFuture);
        return allFutures.thenApply((Void) -> {  // 用thenRun()的话没法return
            Optional<Order> orderOptional = orderOptionalFuture.join();
            if (!orderOptional.isPresent())
                throw new OrderNotFoundException(orderId);

            Order order = orderOptional.get();

            // check userId is the owner of this order
            User user = order.getUser();
            if (user.getUserId() != userId)
                throw new NotAuthorizedException();

            // Building UserOrderResponse
            ServiceStatus serviceStatus = new ServiceStatus(true);
            UserResponse userResponse = new UserResponse(user.getUsername(), user.getEmail());

            OrderProduct op = orderProductFuture.join().get();
            Product product = productService.getProductById(op.getProduct().getProductId()).get();
            OrderItemResponse oir = new OrderItemResponse(product.getDescription(), op.getPurchasedQuantity());
            List<OrderItemResponse> orderItemResponseList = Arrays.asList(oir);
            OrderResponse orderResponse = new OrderResponse(order.getOrderId(), order.getPlacementTime(), op.getExecutionRetailPrice(), orderItemResponseList);

            return new UserOrderResponse(serviceStatus, orderResponse, userResponse);
        });
    }
}
