package com.bfs.restfulonlineshopping.entity.request;

import lombok.Getter;
import lombok.Setter;

// Product: description, wholesale_price, retail_price and stock’s quantity.

@Getter
@Setter
public class AddProductRequest {
    // 不是不能用RequestParam一个个加 但是用Request封装起来的话可以force user用对的格式
    private String description;
    private double wholesalePrice;
    private double retailPrice;
    private int quantity;
}
