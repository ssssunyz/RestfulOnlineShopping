package com.bfs.restfulonlineshopping.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
// this class is made for the Asynchronous homework
public class OrderItemResponse {
    private String itemName;
    private int quantity;
}
