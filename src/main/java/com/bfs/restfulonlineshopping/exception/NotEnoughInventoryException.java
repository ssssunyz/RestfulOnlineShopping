package com.bfs.restfulonlineshopping.exception;

public class NotEnoughInventoryException extends Exception {
    public NotEnoughInventoryException() {
        super("Maximum quantity reached. Please decrease quantity. ");
    }
}