package com.proxy.service.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.proxy.service.entity.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JwtUtil {

    public static final String BEARER = "Bearer ";
    public static final String HEADER_AUTH = "Authorization";
    public static final String SECRET_KEY = "ysmimaaKeySecret";
    public static final String AUTHORITY = "roles";
    public static final String HEADER_ACCESS_ERROR = "access-error";
    public static final String HEADER_ACCESS_TOKEN = "access-token";
    public static final String HEADER_REFRESH_TOKEN = "refresh-error";
    public static final String HEADER_CONTENT_TYPE = "application/json";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String REFRESH_TOKEN_URI = "/api/refreshToken";


    public static Map<String, String> getTokens(HttpServletRequest request, Authentication authResult) {
        User user = (User) authResult.getPrincipal();
        Algorithm secretKey = Algorithm.HMAC256(JwtUtil.SECRET_KEY);
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() * 5 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(JwtUtil.AUTHORITY, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(secretKey);

        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() * 30 * 24 * 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(secretKey);

        return getIdsToken(jwtRefreshToken, jwtAccessToken);
    }

    public static void checkAuthorization(String authorizationToken) {
        String token = authorizationToken.substring(7);
        Algorithm algorithm = Algorithm.HMAC256(JwtUtil.SECRET_KEY);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decode = jwtVerifier.verify(token);
        String username = decode.getSubject();

        List<SimpleGrantedAuthority> roles = Stream.of(decode.getClaim(JwtUtil.AUTHORITY).asArray(String.class))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, roles);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    public static Map<String, String> getIdsToken(String jwtRefreshToken, String jwtAccessToken) {
        Map<String, String> idsToken = new HashMap<>();
        idsToken.put(JwtUtil.HEADER_ACCESS_TOKEN, jwtAccessToken);
        idsToken.put(JwtUtil.HEADER_REFRESH_TOKEN, jwtRefreshToken);
        return idsToken;
    }

    public static String generateNewToken(HttpServletRequest request, Algorithm algorithm, String username, com.proxy.service.entity.User userByUsername) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() * 5 * 60 * 100))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(JwtUtil.AUTHORITY, userByUsername.getRoles().stream()
                        .map(Role::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }
}
