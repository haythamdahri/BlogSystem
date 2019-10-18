package com.platform.blog.services;

import com.platform.blog.entities.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private SecurityRoleService securityRoleService;

    /*
    * @Following method will be invoced by Spring Security to filter the user
    * to verify the credientials using basic authentication with email and password or with JWT
    */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Retrieve security user from database using security user service instance
        // We are using email instead of username because we based our authentication on email
        SecurityUser securityUser = this.securityUserService.getSecurityUser(email);
        if( securityUser == null ) {
            throw new UsernameNotFoundException("Authentication failed");
        }
        // In case the user exists, we need to create a collection of type GrantedAuthorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        securityUser.getSecurityRoles().forEach(securityRole -> {
            authorities.add(new SimpleGrantedAuthority(securityRole.getRole().name()));
        });
        // After constructing the list of authorities, we will return an instance
        // of spring security user giving it username, password and authorities
        return new User(securityUser.getEmail(), securityUser.getPassword(), authorities);
    }
}
