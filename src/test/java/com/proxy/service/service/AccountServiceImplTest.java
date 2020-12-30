package com.proxy.service.service;

import com.proxy.service.entity.Role;
import com.proxy.service.entity.User;
import com.proxy.service.repository.RoleRepository;
import com.proxy.service.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.Assert;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AccountService accountService = new AccountServiceImpl(roleRepository, userRepository,null);

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void should_create_user() {
        //Given
        User userToCreate = User.builder().id(1L)
                .username("username")
                .password("password")
                .email("email")
                .build();
        //when
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(userToCreate);

        User createdUser = accountService.createUser(userToCreate);

        //Then
        Assertions.assertThat(userToCreate).isEqualTo(createdUser);

        Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));

    }

    @Test
    void should_find_all_users() {
        //Given
        List<User> users = Arrays.asList(
                User.builder().id(1L)
                        .username("username")
                        .password("password")
                        .email("email")
                        .build()
        );
        //when
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<User> returnedUserList = accountService.findAllUsers();

        //Then
        Assertions.assertThat(returnedUserList).contains(users.get(0));

        Mockito.verify(userRepository, Mockito.times(1)).findAll();


    }

    @Test
    void should_find_user_by_username() {
        //Given
        User userToFind = User.builder().id(1L)
                .username("username")
                .password("password")
                .email("email")
                .build();
        //when
        Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(userToFind);
        User userFound = accountService.findUserByUsername("username");

        //Then
        Assertions.assertThat(userFound).isEqualTo(userFound);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByUsername(ArgumentMatchers.anyString());

    }

    @Test
    void should_delete_user_by_id() {
        //Given
        User userToDelete = User.builder().id(1L)
                .username("username")
                .password("password")
                .email("email")
                .build();
        //when
        Mockito.doNothing().when(userRepository).deleteById(ArgumentMatchers.anyLong());
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(userToDelete));

        User userFound = accountService.deleteUserById(userToDelete.getId());

        //Then
        Assertions.assertThat(userFound).isEqualTo(userFound);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(ArgumentMatchers.anyLong());

    }

    @Test
    void should_assign_role_to_user() {
        //Given
        Role roleAdmin = Role.builder()
                .authority("ADMIN")
                .build();
        User userToFind = User.builder().id(1L)
                .username("username")
                .password("password")
                .email("email")
                .roles(new ArrayList<>(Arrays.asList(
                        roleAdmin)))
                .build();
        //when
        Mockito.when(userRepository.findUserByUsername(ArgumentMatchers.anyString())).thenReturn(userToFind);
        Mockito.when(roleRepository.findRoleByAuthority(ArgumentMatchers.anyString())).thenReturn(roleAdmin);

        accountService.assignRoleToUser("username", "admin");

        //Then
        Assertions.assertThat(userToFind.getRoles()).contains(roleAdmin);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByUsername(ArgumentMatchers.anyString());
        Mockito.verify(roleRepository, Mockito.times(1)).findRoleByAuthority(ArgumentMatchers.anyString());

    }

    @Test
    void should_create_role() {
        //Given
        Role roleToCreate = Role.builder().id(1L)
                .authority("ADMIN")
                .build();
        //when
        Mockito.when(roleRepository.save(ArgumentMatchers.any(Role.class))).thenReturn(roleToCreate);

        Role createdRole = accountService.createRole(roleToCreate);

        //Then
        Assertions.assertThat(roleToCreate).isEqualTo(createdRole);

        Mockito.verify(roleRepository, Mockito.times(1)).save(ArgumentMatchers.any(Role.class));

    }
}