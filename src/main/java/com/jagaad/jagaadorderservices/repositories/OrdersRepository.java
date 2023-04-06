package com.jagaad.jagaadorderservices.repositories;



import com.jagaad.jagaadorderservices.entities.Orders;
import com.jagaad.jagaadorderservices.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, Long> {
    Orders findFirstByIdAndUserIdAndCreatedAtAfter(Long id, Long userId, Date created);



    Orders findFirstByIdAndUserIdAndStatus(Long id, Long userId, String status);


    Orders findFirstByCartIdAndStatus(Long id, String status);


    List<Orders> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Orders> findAllByUserLinkIn(List<Users> users);

}
