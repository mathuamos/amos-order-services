package com.jagaad.jagaadorderservices.services;

import com.jagaad.jagaadorderservices.entities.Role;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.repositories.RoleRepository;
import com.jagaad.jagaadorderservices.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.stream;

@Service("customUserApiDetailsService")
@Log4j2

@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    final
    RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users saveUser(Users users){

       users.setPassword(passwordEncoder.encode( users.getPassword()));
        return userRepository.save(users);
    }

    public void addRoleToUser(String username, String roleName) {

        Users user= userRepository.findByEmail(username).orElse(null);
        Role role=roleRepository.findByName(roleName);


        assert user != null;

        user.getRoles().add(role);


    }
    public Users findUserByUsername(String username){
     return    userRepository.findByEmail(username).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user= userRepository.findByEmail(username).orElse(null);


        if (null == user) {
            throw new UsernameNotFoundException("No such user exists");
        }

        return buildAuthDetails(user);
    }


    private UserDetails buildAuthDetails(Users user) {
        // Iterate the list
        List<GrantedAuthority> result = new ArrayList<>();
        List<String> userRoles = new ArrayList<>();
        String field;

        Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();

        user.getRoles().forEach(role->{
            authorities.add(new SimpleGrantedAuthority(role.getName()));

        });
      //  authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));


        return new User(user.getEmail(), user.getPassword(), true,
                true, true, true, authorities);
    }



}
