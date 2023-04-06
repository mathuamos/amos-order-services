package com.jagaad.jagaadorderservices.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilsTest {

    @Test
    public void testGetCurrentUserLogin() {
        // Create a mock user
        User user = new User("johndoe", "password", new ArrayList<>());

        // Set up the authentication and security context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        // Call the method being tested
        String currentUserLogin = SecurityUtils.getCurrentUserLogin();

        // Verify the result
        assertThat(currentUserLogin).isEqualTo("johndoe");
    }
}