package com.bfs.restfulonlineshopping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "user_id")
//    @Type(type = "org.hibernate.type.UUIDCharType")
//    private UUID userId;
    private Integer userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password" , nullable = false)
    private String password;

//    @Column(name = "is_admin")
//    private boolean isAdmin;

    @OneToOne
    @JoinColumn(name = "fk_watchlist")
    // @JoinColumn is used on the owning side, specifying the name of the foreign key column
    private Watchlist watchlist;

    // 有mappedBy的是reference side: any changes made to the inverse side will be ignored
    /* cascade = CascadeType.ALL:
        session.save(user)的时候, any changes made to orders也会被persist
        如果remove user, associated order也会remove
     */
    @ToString.Exclude
    @JsonIgnore  // reference side
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    // each user can have a list of permissions
    @ManyToMany  // owning side
    // 注意这里是JoinTable而不是JoinColumn
    @JoinTable(name = "user_permission",
            joinColumns = {@JoinColumn(name = "fk_user")},  // 在user_permission表里 本class(User)的column名
            inverseJoinColumns = {@JoinColumn(name = "fk_permission")})
    private List<Permission> permissions;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}