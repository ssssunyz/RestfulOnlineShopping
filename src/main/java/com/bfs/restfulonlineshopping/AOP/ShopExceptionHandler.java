package com.bfs.restfulonlineshopping.AOP;

import com.bfs.restfulonlineshopping.entity.response.ErrorResponse;
import com.bfs.restfulonlineshopping.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ShopExceptionHandler {

    @ExceptionHandler(value = {InvalidCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e){
        // ErrorResponse.builder().message(e.getMessage()).build().getClass()  // ErrorResponse
        // build() return的是这个class的object 所以signature里面要写ResponseEntity<ErrorResponse>
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ProductNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NotEnoughInventoryException.class})
    public ResponseEntity<ErrorResponse> handleNotEnoughInventoryException(NotEnoughInventoryException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {OrderNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {CancelingCompletedOrderException.class})
    public ResponseEntity<ErrorResponse> handleCancelingCompletedOrderException(CancelingCompletedOrderException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NotAuthorizedException.class})
    public ResponseEntity<ErrorResponse> handleNotAuthorizedException(NotAuthorizedException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.UNAUTHORIZED);
    }

//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity handleGeneralException(Exception e){
//        return new ResponseEntity(ErrorResponse.builder().message("Custom General Exception. ").build(), HttpStatus.OK);
//    }
}