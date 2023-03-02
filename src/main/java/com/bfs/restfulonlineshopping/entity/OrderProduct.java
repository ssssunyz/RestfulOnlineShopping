package com.bfs.restfulonlineshopping.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

/* 直接在Order.class里用hibernate 的@JoinTable的话 就只有两个fk field
   新建一个order_product可以加custom columns
   然后把Order和Product里面的mapping改成对order_product是OneToMany
   而不是像以前一样Order和Product 直接 ManyToMany
 */
@Entity
@Table(name = "order_product")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderProductId;

    @ManyToOne
    @JoinColumn(name = "fk_order")  // owning side
    private Order order;

    @ManyToOne
    @JoinColumn(name = "fk_product")  // owning side
    private Product product;

    // Add any custom columns you need
    @Column(name = "purchased_quantity", nullable = false)
    private int purchasedQuantity;

    @Column(name = "execution_retail_price", nullable = false)
    private double executionRetailPrice;

    @Column(name = "execution_wholesale_price", nullable = false)
    private double executionWholesalePrice;

    public OrderProduct(Order order, Product product, int purchasedQuantity, double executionRetailPrice, double executionWholesalePrice) {
        this.order = order;
        this.product = product;
        this.purchasedQuantity = purchasedQuantity;
        this.executionRetailPrice = executionRetailPrice;
        this.executionWholesalePrice = executionWholesalePrice;
    }
}
