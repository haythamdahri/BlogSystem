package com.platform.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
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

//@Configuration
//@EnableWebSecurity
//@EnableAutoConfiguration
//@ComponentScan(basePackages = {"com.platform.blog"})
///*
//* @EnableGlobalMethodSecurity(securedEnabled = true) is used to protect any method with @Secured which we add to specify authorized users based on there roles
//* */
//@EnableGlobalMethodSecurity(securedEnabled = true)
//// Order(1) means the main authentication is this,
//// Order(2) will not handle anything because the traffic will be hold here and never rich the second one.
//@Order(2)
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
////    // Create granted static users to allow them accessing to the application
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////       // Basic Authentication
////        auth.inMemoryAuthentication()
////                .withUser("haytham")
////                .password("{bcrypt}$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e")
////                .roles("ADMIN", "MANAGER", "USER")
////                .and()
////                .withUser("MANAGER")
////                .password("{bcrypt}$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e")
////                .credentialsExpired(true)
////                .accountExpired(true)
////                .accountLocked(true)
////                .authorities("WRITE_PRIVILEGES", "READ_PRIVILEGES")
////                .and()
////                .withUser("asmae")
////                .password("{bcrypt}$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e")
////                .roles("USER");
////    }
//
//
//
//    // Database authentication
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
//
//
//        System.out.println("USING CONFIGURE GLOBAL METHOD TO AUTHENTICATE USERS");
//        auth.jdbcAuthentication()
//                .dataSource(this.dataSource).passwordEncoder(new BCryptPasswordEncoder())
//                .usersByUsernameQuery("select email as principal, password as credentials, enabled from security_users where email=?")
//                .authoritiesByUsernameQuery("select user_email as principal, role_role as role from security_users_roles where user_email=?");
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/error", "/css/**", "/js/**", "/images/**", "/less/**", "/metadata/**", "/scss/**", "/sprites/**", "/svgs/**", "/webfonts/**");
//    }
//
//    // Configure access security by defining a login page and login processing url
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // Allow only admins to access the api
////        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/**").hasAuthority("ROLE_ADMIN");
//
//
//        http.authorizeRequests()
//                .antMatchers("/", "/posts/*", "/media/**", "/register/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll()
//                .failureUrl("/login?error")
//                .defaultSuccessUrl("/")
//                .loginProcessingUrl("/authenticate")
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage("/access-denied");
//
//
//    }



}

