package com.bfs.restfulonlineshopping.entity;

import lombok.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderDetails {
    private Timestamp placementTime;
    private String status;
    private String productDescription;
    private double executionRetailPrice;
    private int purchasedQuantity;
    private String userEmail;

    public OrderDetails(Timestamp placementTime, String status, String productDescription,
                        double executionRetailPrice, int purchasedQuantity) {
        this.placementTime = placementTime;
        this.status = status;
        this.productDescription = productDescription;
        this.executionRetailPrice = executionRetailPrice;
        this.purchasedQuantity = purchasedQuantity;
    }

    public OrderDetails(Timestamp placementTime, String status, String productDescription,
                        double executionRetailPrice, int purchasedQuantity, String userEmail) {
        this.placementTime = placementTime;
        this.status = status;
        this.productDescription = productDescription;
        this.executionRetailPrice = executionRetailPrice;
        this.purchasedQuantity = purchasedQuantity;
        this.userEmail = userEmail;
    }
}
