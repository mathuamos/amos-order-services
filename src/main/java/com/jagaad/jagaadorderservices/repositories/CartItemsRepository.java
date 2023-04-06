package com.shoppingcart.repositories;


import com.shoppingcart.entities.CartItems;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepository extends CrudRepository<CartItems,Long> {

    CartItems findFirstByCartIdAndProductIdAndStatus(Long cartId, Long productId, String status);
    List<CartItems> findAllByCartIdAndStatus(Long cartId, String status);
}
