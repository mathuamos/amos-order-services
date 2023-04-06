package com.jagaad.jagaadorderservices.repositories;

import com.jagaad.jagaadorderservices.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role ,Long>{

    Role findByName(String name);

}
