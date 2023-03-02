package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.request.LoginRequest;
import com.bfs.restfulonlineshopping.entity.response.LoginResponse;
import com.bfs.restfulonlineshopping.exception.InvalidCredentialsException;
import com.bfs.restfulonlineshopping.security.AuthUserDetail;
import com.bfs.restfulonlineshopping.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/OnlineShop/login")
public class LoginController {

    /* - AuthenticationManager performs authentication process based on the supplied AuthenticationProvider
            (可以有很多provider, manager会自己loop through)
       - 其中一个provider在config里面写了 是DaoAuthenticationProvider的那个Bean
       - 我们写了一个UserService which implements userDetailsService, 在config的时候注入给SpringSecurityConfig了
            然后provider.setUserDetailsService(userDetailsService);
            It uses the userDetailsService by calling the loadUserByUsername();
     */
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    // User trying to log in with username and password
    // @PostMapping("/login")
    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {

        System.out.println("---------In Login Controller--------");
        Authentication authentication;  // holds the authenticated user

        // Try to authenticate the user using the username and password
        try {
                /* - authenticationManager.authenticate(Authentication)
                   - Authentication Manager decides which Authentication Provider to delegate the call to
                     by calling the supports() method on every available AuthenticationProvider.
                     If the supports() method returns true, then that AuthenticationProvider supports
                     the Authentication type and is used to perform authentication.
                   - DaoAuthenticationProvider 就support UsernamePasswordAuthenticationToken
                   - DaoAuthenticationProvider 会call UserService里的loadUserByUsername, which return一个UserDetail
                   - 下面这句就把这个UserDetail存在authentication里面 (用getPrincipal()得到)
                 */
                authentication = authenticationManager.authenticate(
                        /* UsernamePasswordAuthenticationToken implements Authentication
                           which specifies that the user wants to authenticate using a username and password */
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();

            // throw new BadCredentialsException("Provided credential is invalid.");
            /* ↑这个不需要在signature里面throws是因为它是RuntimeException
               而我们的InvalidCredentialsException 是implement的Exception */
        }

        /* - Successfully authenticated user will be stored in an AuthUserDetail object.
           - AuthUserDetail是自己写的一个class, implements the UserDetails interface and
             is used to hold the username and authorities of the authenticated user
             which will be encrypted in the jwt */
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal(); // getPrincipal() returns the user object

        // A token wil be created using:
        // 1. username/email/userId (used username in this case)
        // 2. permissions (userDetails.getAuthorities())
        String token = jwtProvider.createToken(authUserDetail);

        // Returns the token as a response to frontend/postman
        return LoginResponse.builder()
                .message("Welcome! " + authUserDetail.getUsername())
                .token(token)  // string
                .build();
    }
}