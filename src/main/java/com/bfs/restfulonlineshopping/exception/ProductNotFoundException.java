package com.bfs.restfulonlineshopping.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException() {
        super("No Product Found. ");
    }
}
