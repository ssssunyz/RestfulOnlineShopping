package com.bfs.restfulonlineshopping.exception;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() {
        super("You are not the owner of the resource you are requesting. ");
    }
}