package com.bfs.restfulonlineshopping.service;

import com.bfs.restfulonlineshopping.dao.OrderProductDao;
import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.OrderProduct;
import com.bfs.restfulonlineshopping.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProductService {
    private OrderProductDao orderProductDao;

    @Autowired
    public OrderProductService(OrderProductDao orderProductDao) {
        this.orderProductDao = orderProductDao;
    }

    public void commitPurchase(Order order, Product product, int purchasedQuantity, double retailPrice, double wholesalePrice) {
        orderProductDao.commitPurchase(order, product, purchasedQuantity, retailPrice, wholesalePrice);
    }

    public Optional<OrderProduct> getOrderProductByOrderId(UUID orderId) {
        return orderProductDao.getOrderProductByOrderId(orderId);
    }

    // this method is made for the Asynchronous homework
    @Async("taskExecutor")
    public CompletableFuture<Optional<OrderProduct>> getOrderProductByOrderIdAsync(UUID orderId) {
        Optional<OrderProduct> orderProductOptional = orderProductDao.getOrderProductByOrderId(orderId);
        return CompletableFuture.completedFuture(orderProductOptional);
    }

//    public Map<String, Integer> amountSoldByProduct() {
//        return orderProductDao.amountSoldByProduct();
//    }
}