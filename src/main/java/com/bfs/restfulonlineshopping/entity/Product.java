package com.bfs.restfulonlineshopping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private int productId;

    @Column
    private String description;

    @Column(name = "wholesale_price")
    private double wholesalePrice;

    @Column(name = "retail_price")
    private double retailPrice;

    @Column
    private int quantity;

    @ToString.Exclude
    @JsonIgnore  // reference side
//    @ManyToMany(mappedBy = "boughtProducts", cascade = CascadeType.ALL)
//    private List<Order> orders;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    @ToString.Exclude
    @JsonIgnore  // reference side
    @ManyToMany(mappedBy = "watchedProducts", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @ManyToMany(mappedBy = "watchedProducts", fetch = FetchType.EAGER)
    private List<Watchlist> watchlists;

    public Product(String description, double wholesalePrice, double retailPrice, int quantity) {
        this.description = description;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }

    // ProductDao 的 getAllProducts_User 的 cq.multiselect会用到
    public Product(int productId, String description, double retailPrice) {
        this.productId = productId;
        this.description = description;
        this.retailPrice = retailPrice;
    }

}