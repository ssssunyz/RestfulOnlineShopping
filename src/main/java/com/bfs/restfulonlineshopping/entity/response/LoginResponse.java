package com.bfs.restfulonlineshopping.entity.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String message;
    private String token;
}