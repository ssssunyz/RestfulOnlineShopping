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
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "permission_id", nullable = false)
    private int permissionId;

    @Column
    private String role;

    @ToString.Exclude
    @JsonIgnore  // reference side
    @ManyToMany(mappedBy = "permissions", cascade = CascadeType.ALL, fetch = FetchType.EAGER)  // 名字 = User table下面的permissions field
    private List<User> users;

    public Permission(String role) {
        this.role = role;
    }
}
