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
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "watchlist_id", nullable = false)
    private int watchlistId;

    @ToString.Exclude
    @JsonIgnore  // reference side
    @OneToOne(mappedBy = "watchlist", cascade = CascadeType.ALL)  // reference side
    //@JoinColumn(name = "user_fk")
    private User user;

    @ManyToMany  // owning side
    @JoinTable(name = "watchlist_product",
            joinColumns = {@JoinColumn(name = "fk_watchlist")},
            inverseJoinColumns = {@JoinColumn(name = "fk_product")})
    private List<Product> watchedProducts;
}