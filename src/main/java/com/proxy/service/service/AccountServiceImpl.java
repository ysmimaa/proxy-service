package com.proxy.service.service;

import com.proxy.service.entity.Role;
import com.proxy.service.entity.User;
import com.proxy.service.repository.RoleRepository;
import com.proxy.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AccountServiceImpl(RoleRepository roleRepository, UserRepository userRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (user != null) {
            String pw = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(pw);
            return userRepository.save(user);
        }
        throw new RuntimeException("Please can you provide a valid user");
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        if (!username.isEmpty()) {
            return userRepository.findUserByUsername(username);
        }
        throw new RuntimeException("Please can you provide a valid user");
    }

    @Override
    public User deleteUserById(Long id) {
        if (id != null) {
            Optional<User> foundUser = userRepository.findById(id);
            foundUser.ifPresent(user -> {
                userRepository.deleteById(user.getId());
            });
            return foundUser.orElse(null);
        }
        throw new RuntimeException("Please can you provide a valid id user");
    }

    @Override
    public void assignRoleToUser(String username, String authority) {
        if (username.isEmpty() && authority.isEmpty()) {
            throw new RuntimeException("Please provide a valid username or role");
        }
        User userByUsername = userRepository.findUserByUsername(username);
        Role roleByAuthority = roleRepository.findRoleByAuthority(authority);
        userByUsername.getRoles().add(roleByAuthority);
    }

    @Override
    public Role createRole(Role role) {
        if (role != null) {
            return roleRepository.save(role);
        }
        throw new RuntimeException("Please can you provide a valid role");
    }
}
