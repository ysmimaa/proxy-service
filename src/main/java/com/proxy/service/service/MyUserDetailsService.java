package com.proxy.service.service;

import com.proxy.service.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private AccountService accountService;

    public MyUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFoundByUsername = accountService.findUserByUsername(username);
        if (userFoundByUsername != null && userFoundByUsername.getUsername() != null) {
            List<SimpleGrantedAuthority> grantedAuthorities = userFoundByUsername.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(userFoundByUsername.getUsername(),
                    userFoundByUsername.getPassword(), grantedAuthorities);
        }
        throw new RuntimeException("Username does not exist");
    }

}
