package com.bfs.restfulonlineshopping.service;

import com.bfs.restfulonlineshopping.dao.UserDao;
import com.bfs.restfulonlineshopping.entity.Permission;
import com.bfs.restfulonlineshopping.entity.User;
import com.bfs.restfulonlineshopping.entity.Watchlist;
import com.bfs.restfulonlineshopping.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    // loadUserByUsername()是UserDetailsService的method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.loadUserByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Username does not exist. ");
        }

        // does exist
        User user = userOptional.get();  // database user
        return AuthUserDetail.builder()  // our AuthUserDetail implements spring security's UserDetails
                .username(user.getUsername())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))  // authorities(List<GrantedAuthority>)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    // simplified version of the above method
    public User getUserByUsername(String username) {
        return userDao.loadUserByUsername(username).get();
    }

    @Transactional
    public boolean userExists(String username, String email) {
        System.out.println("In UserService: userExists");
        Optional<User> userOptional1 = userDao.loadUserByUsername(username);
        if (userOptional1.isPresent()) return true;

        Optional<User> userOptional2 = userDao.loadUserByEmail(email);
        return userOptional2.isPresent();
    }

    @Transactional
    public void createUser(String username, String email, String password) {
        userDao.createUser(username, email, password);
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();

        for (Permission permission: user.getPermissions()){
            String role = permission.getRole();
            userAuthorities.add(new SimpleGrantedAuthority(role));
            // SimpleGrantedAuthority can be created from any custom role Strings
        }

        return userAuthorities;
    }

    public Watchlist viewWatchlist(int userId) {
        return userDao.viewWatchlist(userId);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}