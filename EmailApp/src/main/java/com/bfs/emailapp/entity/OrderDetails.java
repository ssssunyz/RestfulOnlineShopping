package com.bfs.emailapp.entity;

import lombok.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
//@Builder
@ToString
public class OrderDetails {
    private Timestamp placementTime;
    private String status;
    private String productDescription;
    private double executionRetailPrice;
    private int purchasedQuantity;
    private String userEmail;
}