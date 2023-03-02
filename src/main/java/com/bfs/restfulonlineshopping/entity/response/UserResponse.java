package com.bfs.restfulonlineshopping.entity.response;

import lombok.Getter;
import lombok.ToString;

// this class is made for the Asynchronous homework
@ToString
@Getter
public class UserResponse {
    private String username;
    private String email;

    public UserResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
