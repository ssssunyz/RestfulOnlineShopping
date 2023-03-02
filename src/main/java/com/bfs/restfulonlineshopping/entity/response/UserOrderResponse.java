package com.bfs.restfulonlineshopping.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// this class is made for the Asynchronous homework
@AllArgsConstructor
@ToString
@Getter  // 不写getter的话会有HttpMediaTypeNotAcceptableException
public class UserOrderResponse {
    private ServiceStatus serviceStatus;
    private OrderResponse orderResponse;
    private UserResponse userResponse;
}
