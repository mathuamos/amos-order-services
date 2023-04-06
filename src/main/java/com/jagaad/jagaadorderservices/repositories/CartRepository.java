package com.jagaad.jagaadorderservices.repositories;



import com.jagaad.jagaadorderservices.entities.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart,Long> {

    Cart findFirstByUserIdAndStatus(Long userId, String status);
}
