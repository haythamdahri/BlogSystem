package com.platform.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.blog.entities.security.SecurityUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;



/*
* @Used to authenticate user throught a separated user wihtout CSRF (Front-end user)
*/
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SecurityUser securityUser = null;
        // If data are sent using other format than JSON, we can retrieve data using the request object as we always do
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        try {
            // Retrieve SecurityUser object from request json data using Jackson dependency (mapper)
            securityUser = new ObjectMapper().readValue(request.getInputStream(), SecurityUser.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        /* In case we successflly retrieved data from the request, we will authentocate the user using
         * the AuthenticationManager instance giving it the email (username if not our case) and password
         */
        System.out.println("*******************************************************************");
        System.out.println("Email: " + securityUser.getEmail());
        System.out.println("Password: " + securityUser.getPassword());
        System.out.println("*******************************************************************");
        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(securityUser.getEmail(), securityUser.getPassword())
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // Retrieve the user from AuthResult object passed as argument (Spring user and not SecurityUser)
        // Build jwt Token from Jwts class
        // The subject must be the username(The email in our case)
        // which help us to decode it when receiving the token on the Frontend side
        // Security Constants is a created class which contains only constant properties
        // claim method for additions

        System.out.println(authResult.getPrincipal().getClass().getSimpleName());

        User springUser = (User) authResult.getPrincipal();
        System.out.println("*****************************************************");
        System.out.println("Email: " + springUser.getUsername());
        System.out.println("Password: " + springUser.getPassword());
        System.out.println("*****************************************************");
        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
                .claim("roles", springUser.getAuthorities())
                .compact();
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwtToken);
    }
}
