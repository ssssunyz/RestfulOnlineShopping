package com.bfs.restfulonlineshopping.exception;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException() {
        super("Incorrect credentials, please try again. ");
    }
}