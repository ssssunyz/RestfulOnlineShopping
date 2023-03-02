package com.bfs.restfulonlineshopping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

@Entity
@Table(name = "Orders")  // 命名不能用Order 是reserved keyword
public class Order {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "order_id")
//    private int orderId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID orderId;

    @Column(name = "placement_time", nullable = false)
    private Timestamp placementTime;

    @Column
    private String status;

    @ManyToOne  // owning side
    @JoinColumn(name = "fk_user")
    // 没有mappedBy的是owner: owns the foreign key in the database, responsible for managing the relationship
    private User user;

//    @ManyToMany  // owning side
//    @JoinTable(name = "order_product",
//            joinColumns = {@JoinColumn(name = "fk_order")},
//            inverseJoinColumns = {@JoinColumn(name = "fk_product")})
//    private List<Product> boughtProducts;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    public Order(Timestamp placementTime, String status, User user) {
        this.placementTime = placementTime;
        this.status = status;
        this.user = user;
    }
}