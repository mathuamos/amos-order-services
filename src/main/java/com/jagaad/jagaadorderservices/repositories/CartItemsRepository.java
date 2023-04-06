package com.jagaad.jagaadorderservices.repositories;



import com.jagaad.jagaadorderservices.entities.CartItems;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepository extends CrudRepository<CartItems,Long> {


    CartItems findFirstByIdAndCartIdAndStatus(Long id, Long cartId, String status);

    List<CartItems> findAllByCartIdAndStatus(Long cartId, String status);


    Integer countAllByCartIdAndStatus(Long cartId, String status);
}



