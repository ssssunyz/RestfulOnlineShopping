package com.bfs.restfulonlineshopping.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

// this class is made for the Asynchronous homework
@AllArgsConstructor
@Getter
@ToString
public class OrderResponse {
    private UUID orderId;
    private Timestamp time;
    private double totalPrice;
    private List<OrderItemResponse> orderItemResponseList;

//    @Override
//    public String toString() {
//
//    }
}
