package com.example.stage1Hng.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private  final JWTConfig jwtConfig;
    private  final CustomUserDetailsService customUserDetailsService;


    @Autowired
    public JWTAuthFilter(JWTConfig jwtConfig, CustomUserDetailsService customUserDetailsService) {
        this.jwtConfig = jwtConfig;
        this.customUserDetailsService = customUserDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = getJWT(request);
        if (StringUtils.hasText(token) && jwtConfig.validateToken(token)) {
            String username = jwtConfig.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        filterChain.doFilter(request, response);
    }

    private String getJWT(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader!=null &&(StringUtils.hasText("bearerToken") && authHeader.startsWith("Bearer "))) {
            return authHeader.substring(7);
        }
        return null;
    }
}
