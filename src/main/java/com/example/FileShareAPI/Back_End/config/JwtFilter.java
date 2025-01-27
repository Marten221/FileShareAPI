package com.example.FileShareAPI.Back_End.config;

import com.example.FileShareAPI.Back_End.exception.UnAuthorizedException;
import com.example.FileShareAPI.Back_End.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import utils.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = authorize(token, request);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>())
            );
        }
        chain.doFilter(request, response);
    }

    /**
     * Method for validating tokens. If the token is invalid, but the endpoint is public, then the user may be let through,
     * although with an empty userId, meaning that he can access only public resources.
     * @param token
     * @param request
     * @return
     */
    protected String authorize(String token, HttpServletRequest request) {
        String userId;
        try {
            userId = JwtUtil.validateToken(token); // if the token is not valid, an exception gets thrown and access is not authorized
        } catch (UnAuthorizedException e) {
            if (request.getRequestURI().startsWith("/public")) {
                userId = "anonymous";
            } else {
                throw e;
            }
        }

        return userId;
    }
}
