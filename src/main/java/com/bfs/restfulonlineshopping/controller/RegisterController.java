package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.request.RegisterRequest;
import com.bfs.restfulonlineshopping.entity.response.GeneralResponse;
import com.bfs.restfulonlineshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/OnlineShop/register")
public class RegisterController {

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public GeneralResponse register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("---------In Register Controller--------");

        // check if user already exists
        boolean exist = userService.userExists(registerRequest.getUsername(), registerRequest.getEmail());
        if (exist) {
            return GeneralResponse.builder()
                    .message("User Already Exist! Please try a different username or email. ").build();
        }

        // store to database
        userService.createUser(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());

        return GeneralResponse.builder()
                .message("Successfully Registered! Please Use these Credentials to Log In. ").build();
    }
}
