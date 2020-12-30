package com.proxy.service.filter;

import com.proxy.service.common.JwtUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationToken = request.getHeader(JwtUtil.HEADER_AUTH);
        if (request.getRequestURI().equals(JwtUtil.REFRESH_TOKEN_URI)) {
            filterChain.doFilter(request, response);
        } else {
            if (!StringUtils.isEmpty(authorizationToken) && authorizationToken.contains(JwtUtil.BEARER)) {
                try {
                    JwtUtil.checkAuthorization(authorizationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception ex) {
                    response.setHeader(JwtUtil.HEADER_ACCESS_ERROR, ex.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }

    }
}
