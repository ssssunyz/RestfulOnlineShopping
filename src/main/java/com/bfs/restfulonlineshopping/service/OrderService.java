package com.bfs.restfulonlineshopping.service;

import com.bfs.restfulonlineshopping.dao.OrderDao;
import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    private OrderDao orderDao;

    @Autowired
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

//    public void creatOrder(Timestamp placementTime, String status, User user, List<Product> boughtProducts) {
//        orderDao.createOrder(placementTime, status, user, boughtProducts);
//    }

    public Optional<Order> getOrderByID(UUID orderId) {
        return orderDao.getOrderByID(orderId);
    }

    // this method is made for the Asynchronous homework
    @Async("taskExecutor")
    public CompletableFuture<Optional<Order>> getOrderByIdAsync(UUID orderId) {
        Optional<Order> orderOptional = orderDao.getOrderByID(orderId);
        return CompletableFuture.completedFuture(orderOptional);
    }

    public void changeStatus(UUID orderId, String status) {
        orderDao.changeStatus(orderId, status);
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDao.getOrdersByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public List<User> mostSpentUser() {
        return orderDao.mostSpentUser();
    }
}