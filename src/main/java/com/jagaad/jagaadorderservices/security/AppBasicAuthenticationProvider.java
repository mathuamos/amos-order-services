package com.jagaad.jagaadorderservices.security;

import com.jagaad.jagaadorderservices.configs.ApplicationProperties;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.services.UserService;
import com.jagaad.jagaadorderservices.utils.AppFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Component
public class AppBasicAuthenticationProvider extends DaoAuthenticationProvider {



    @Autowired
    private UserService userService;



    @Autowired
    AppFunctions appFunctions;


  @Autowired
 @Qualifier("customUserApiDetailsService")

    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        StringBuilder error = new StringBuilder();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For") == null ? request.getRemoteAddr() : request.getHeader("X-Forwarded-For");

        String username = authentication.getName();


       boolean ifEmailIsValid= appFunctions.validateEmail(username);

       if(!ifEmailIsValid)
           throw new BadCredentialsException("email not valid");

        Users user = userService.findUserByUsername(username);

        if (null == user) {
            error.append("Sorry invalid credentials!");
            throw new BadCredentialsException(error.toString());
        }

        try {
            Authentication auth = super.authenticate(authentication);

            return auth;
        } catch (LockedException e) {
            error.append("User account is locked");
            throw new LockedException(error.toString());
        } catch (UsernameNotFoundException e) {
            error.append("User does not exist");
            throw new UsernameNotFoundException(error.toString());
        } catch (CredentialsExpiredException e) {
            throw new CredentialsExpiredException(error.toString());
        } catch (BadCredentialsException e) {
            error.append("Sorry credentials does't match our records.");
            throw new BadCredentialsException(error.toString());

        }
    }
}
