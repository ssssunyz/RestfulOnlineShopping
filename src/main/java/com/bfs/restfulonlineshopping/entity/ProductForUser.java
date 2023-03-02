package com.bfs.restfulonlineshopping.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

// to hide Product fields for users
public class ProductForUser {
    private int productId;
    private String description;
    private double retailPrice;
}
