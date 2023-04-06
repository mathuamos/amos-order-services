package com.jagaad.jagaadorderservices.services;

import com.jagaad.jagaadorderservices.entities.Role;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.repositories.RoleRepository;
import com.jagaad.jagaadorderservices.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_shouldEncodePassword() {
        // given
        Users user = new Users("John", "Doe", "john@example.com", "password", "1234567890", Collections.emptyList());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(user)).thenReturn(user);

        // when
        Users savedUser = userService.saveUser(user);

        // then
        assertThat(savedUser.getPassword()).isEqualTo("encoded_password");
       // verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addRoleToUser_shouldAddRoleToUser() {
        // given
        Users user = new Users("John", "Doe", "john@example.com", "password", "1234567890", new ArrayList<>());
        Role role = new Role(1L, "ROLE_ADMIN");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(role.getName())).thenReturn(role);

        // when
        userService.addRoleToUser(user.getEmail(), role.getName());

        // then
        assertThat(user.getRoles()).containsExactly(role);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(1)).findByName(role.getName());
    }

    @Test
    void findUserByUsername_shouldReturnUser() {
        // given
        Users user = new Users("John", "Doe", "john@example.com", "password", "1234567890", new ArrayList<>());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when
        Users foundUser = userService.findUserByUsername(user.getEmail());

        // then
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // given
        Users user = new Users("John", "Doe", "john@example.com", "password", "1234567890", new ArrayList<>());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when
        var userDetails = userService.loadUserByUsername(user.getEmail());

        // then
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }


}


