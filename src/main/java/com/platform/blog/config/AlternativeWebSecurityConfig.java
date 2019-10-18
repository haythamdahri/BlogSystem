package com.platform.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.platform.blog"})
/*
* @EnableGlobalMethodSecurity(securedEnabled = true) is used to protect any method with @Secured which we add to specify authorized users based on there roles
*/
//@EnableGlobalMethodSecurity(securedEnabled = true)
// Order(1) means the main authentication is this,
// Order(2) will not handle anything because the traffic will be hold here and never rich the second one.
@Order(1)
public class AlternativeWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    // Database authentication
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{

        // UserDetailsService authentication => Use the instance of userDetailsService to authenticate the user
        auth.userDetailsService(this.userDetailsService)
                .passwordEncoder(this.bCryptPasswordEncoder);


        System.out.println("USING CONFIGURE GLOBAL METHOD TO AUTHENTICATE USERS");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/error", "/css/**", "/js/**", "/images/**", "/less/**", "/metadata/**", "/scss/**", "/sprites/**", "/svgs/**", "/webfonts/**");
    }

    // Configure access security by defining a login page and login processing url
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // JWT authentication
        // csrf must be disabled
        // We will create a STATELESS SessionCreationPolicy
        // We are disabling session authetication (As all use in any website)
        // We need to create a filter as a class named in our case: JWTAuthenticationFIlter
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                // We authorize media, access-denied, login and register only for direct access
                // (For displaying access denied and access to server resource from Front-end application)
                .antMatchers("/media/**", "/access-denied", "/login", "/register")
                .permitAll()
                .antMatchers("/api/**").hasAuthority("ROLE_ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied");
        // Add the created filter to the http object
        // this.authenticationManager() is an inherited method from super class ||
        // addFilterAfter to check if the associated token with the user is authenticated or not
        http.addFilter(new JWTAuthenticationFilter(this.authenticationManager()))
                .addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }



}

