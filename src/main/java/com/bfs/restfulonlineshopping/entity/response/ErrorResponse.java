package com.bfs.restfulonlineshopping.entity.response;

import lombok.*;

// 用custom response可以control什么东西能传过去 这样后面真正传的时候不容易出错
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
}