package com.jagaad.jagaadorderservices.services;

import com.jagaad.jagaadorderservices.entities.Role;
import com.jagaad.jagaadorderservices.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService  {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public Role saveRole(Role role){
        return roleRepository.save(role);
    }
}
