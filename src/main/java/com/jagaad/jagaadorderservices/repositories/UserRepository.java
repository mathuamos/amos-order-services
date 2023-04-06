package com.jagaad.jagaadorderservices.repositories;

import com.jagaad.jagaadorderservices.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<Users,Long> {

    Optional<Users> findByEmail(String email);

    Users findByPhoneNumber(String phoneNumber);

    List<Users> findAllByFirstNameContainsOrLastNameContains(String key1,String key2);
}
