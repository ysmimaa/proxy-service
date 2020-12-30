package com.proxy.service.service;

import com.proxy.service.entity.Role;
import com.proxy.service.entity.User;

import java.util.List;

public interface AccountService {

    User createUser(User user);

    List<User> findAllUsers();

    User findUserByUsername(String username);

    User deleteUserById(Long id);

    void assignRoleToUser(String username, String authority);

    Role createRole(Role role);
}
