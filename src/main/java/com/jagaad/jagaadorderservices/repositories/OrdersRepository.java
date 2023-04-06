package com.shoppingcart.repositories;


import com.shoppingcart.entities.Orders;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, Long> {
    Orders findFirstByIdAndUserId(Long id, Long userId);
}
