package com.proxy.service.resource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.service.common.JwtUtil;
import com.proxy.service.entity.Role;
import com.proxy.service.entity.User;
import com.proxy.service.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostAuthorize("hasAnyAuthority('ADMIN','USER','MODERATOR')")
    @GetMapping(path = "user/findByUsername/{username}")
    public ResponseEntity<User> getUserById(@PathVariable String username) {
        User userFound = accountService.findUserByUsername(username);
        if (userFound != null) {
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(path = "user/deleteById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        if (id != null) {
            User userFound = accountService.deleteUserById(id);
            return new ResponseEntity<>(userFound, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path = "users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> allUsers = accountService.findAllUsers();
        if (!CollectionUtils.isEmpty(allUsers)) {
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (user != null) {
            User createdUser = accountService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "role")
    public ResponseEntity<Role> addUser(@RequestBody Role role) {
        if (role != null) {
            Role createdRole = accountService.createRole(role);
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "account/assignRole/{authority}/toUser/{username}")
    public ResponseEntity<Void> addUser(@PathVariable String authority, @PathVariable String username) {
        if (!username.isEmpty() && !authority.isEmpty()) {
            accountService.assignRoleToUser(username, authority);
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationToken = request.getHeader(JwtUtil.HEADER_AUTH);
        if (!StringUtils.isEmpty(authorizationToken) && authorizationToken.startsWith(JwtUtil.BEARER)) {

            String jwtRefreshToken = authorizationToken.substring(7);
            Algorithm algorithm = Algorithm.HMAC256(JwtUtil.SECRET_KEY);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(jwtRefreshToken);
            String username = decodedJWT.getSubject();

            User userByUsername = accountService.findUserByUsername(username);

            String jwtAccessToken = JwtUtil.generateNewToken(request, algorithm, username, userByUsername);

            response.setContentType(JwtUtil.HEADER_CONTENT_TYPE);
            new ObjectMapper().writeValue(response.getOutputStream(), JwtUtil.getIdsToken(jwtRefreshToken, jwtAccessToken));

        } else {
            throw new RuntimeException("Required a valid token");
        }
    }


}
