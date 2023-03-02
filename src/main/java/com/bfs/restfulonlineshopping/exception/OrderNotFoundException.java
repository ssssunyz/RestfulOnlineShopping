package com.bfs.restfulonlineshopping.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String orderId) {
        super("No Such Order with ID: " + orderId);
    }
}