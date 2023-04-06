package com.payment.gateway.security.config.api;

import com.payment.gateway.security.entities.UserTypes;
import com.payment.gateway.security.entities.Users;
import com.payment.gateway.security.repositories.UserRepository;
import com.payment.gateway.security.repositories.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Date;

/**
 * @author patrick on 7/7/20
 * @project payment
 */
@Component
public class AppBasicAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;


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

        String consumerKey = authentication.getName();

        UserTypes userTypes = userTypeRepository.findFirstByName(UserTypes.API_USER_TYPE);
        if (null == userTypes) {
            error.append("Sorry something is wrong!");
            throw new BadCredentialsException(error.toString());
        }
        Users user = userRepository.findByApiConsumerKeyAndUserType(consumerKey, userTypes.getId());

        if (null == user) {
            error.append("Sorry invalid credentials!");
            throw new BadCredentialsException(error.toString());
        }

        try {
            Authentication auth = super.authenticate(authentication);

            user.setLastLogin(new Date());
            user.setIp(ip);
            userRepository.save(user);

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
            if (userRepository.findByApiConsumerKey(consumerKey) != null) {

                error.append("Sorry credentials does't match our records.");
                throw new BadCredentialsException(error.toString());
            } else {
                error.append("User does not exist");
                throw new UsernameNotFoundException(error.toString());
            }
        }
    }
}
