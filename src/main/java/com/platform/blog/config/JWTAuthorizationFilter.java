package com.platform.blog.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
* @Used to check the user authentication informations before allowing him the access to desired resources
*/
public class JWTAuthorizationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*"); // Allow request from any resource
        response.addHeader("Access-Control-Allow-Headers",
                "Origin, Accept, " +
                        "X-Requested-With, " +
                        "Content-Type, " +
                        "Access-Control-Request-Method, " +
                        "Access-Control-Request-Headers, " +
                        "Authorization");
        response.addHeader("Access-Control-Expose-Headers",
                "Access-Control-Allow-Origin, " +
                        "Access-Control-Allow-Credentials, " +
                        "Authorization");
        if( request.getMethod().equalsIgnoreCase("OPTIONS") ) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        } else {
            String jwtToken = request.getHeader(SecurityConstants.HEADER_STRING);
            if( jwtToken == null || !jwtToken.startsWith(SecurityConstants.TOKEN_PREFIX) ){
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(jwtToken.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody();
            String username = claims.getSubject();
            ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>)claims.get("roles");
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            roles.forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.get("authority")));
            });
            UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            filterChain.doFilter(request, response);
        }
    }


}
