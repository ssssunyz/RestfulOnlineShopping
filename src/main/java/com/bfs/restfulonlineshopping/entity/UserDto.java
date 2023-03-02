package com.bfs.restfulonlineshopping.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserDto {
    private int userId;
    private String username;
    private String email;
}
