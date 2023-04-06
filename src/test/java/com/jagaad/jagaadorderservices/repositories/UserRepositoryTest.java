package com.jagaad.jagaadorderservices.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.jagaad.jagaadorderservices.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jagaad.jagaadorderservices.entities.Users;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindByEmail() {
        String email = "test@test.com";
        Users user = new Users();
        user.setEmail(email);
        Optional<Users> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(email)).thenReturn(optionalUser);

        Optional<Users> foundUser = userRepository.findByEmail(email);

        assertEquals(optionalUser, foundUser);
    }

    @Test
    void testFindByPhoneNumber() {
        String phoneNumber = "1234567890";
        Users user = new Users();
        user.setPhoneNumber(phoneNumber);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(user);

        Users foundUser = userRepository.findByPhoneNumber(phoneNumber);

        assertEquals(user, foundUser);
    }
}
