package com.bfs.restfulonlineshopping.config;

import com.bfs.restfulonlineshopping.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// WebSecurityConfigurerAdapter needs to be extended to override some of its methods
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    /* UserDetailsService 是spring的一个interface
       然后自己写了一个UserService 是 implement UserDetailsService的
     */
    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;

    // 所以应该这边注入的就是UserService?
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    // DaoAuthenticationProvider uses the userDetailsService by calling the loadUserByUsername()
    // supports UsernamePasswordAuthenticationToken (在controller里authenticate的时候会用到)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // This method is used to configure the security of the application
    // Since we are attaching JWT to a request header manually, we don't need to worry about csrf
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/OnlineShop/login", "/OnlineShop/register").permitAll()  // permitAll: 没login也能进; hasAnyAuthority: 得先登录
                .antMatchers("/OnlineShop/admin/*").hasAuthority("admin")
                .antMatchers("/OnlineShop/user/*").hasAuthority("user")
                .anyRequest().authenticated();
    }
}