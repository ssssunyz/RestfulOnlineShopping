package com.bfs.restfulonlineshopping.exception;

public class CancelingCompletedOrderException extends RuntimeException{
    public CancelingCompletedOrderException(String orderId) {
        super("You cannot cancel order: " + orderId + " since it's already completed. ");
    }
}