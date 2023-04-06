package com.jagaad.jagaadorderservices.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jagaad.jagaadorderservices.entities.Orders;
import com.jagaad.jagaadorderservices.entities.Users;
import com.jagaad.jagaadorderservices.repositories.OrdersRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")

public class OrdersRepositoryTest {


    @Mock
    private OrdersRepository ordersRepository;


    @Autowired
    private OrdersRepository ordersRepository1;



    private Orders order1;
    private Orders order2;
    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setFirstName("amos");

        order1 = new Orders();
        order1.setId(1L);
        order1.setUserId(user.getId());
        order1.setUpdatedAt(new Date(2022, 4, 1));

        order2 = new Orders();
        order2.setId(2L);
        order2.setUserId(user.getId());
        order2.setUpdatedAt(new Date(2022, 4, 2));
    }

    @Test
    void testFindFirstByIdAndUserIdAndCreatedAtAfter() {
        Long orderId = 1L;
        Long userId = 1L;
        Date createdAt = new Date();

        Orders expectedOrder = new Orders();
        expectedOrder.setId(orderId);
        expectedOrder.setUserId(userId);
        expectedOrder.setCreatedAt(new Date());

        when(ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(orderId, userId, createdAt))
                .thenReturn(expectedOrder);

        Orders actualOrder = ordersRepository.findFirstByIdAndUserIdAndCreatedAtAfter(orderId, userId,
                createdAt);

        assertEquals(expectedOrder, actualOrder);
    }


    @Test
    public void testFindFirstByIdAndUserIdAndStatus() {
        // Create test data
        Orders order1 = new Orders();
        order1.setId(1L);
        order1.setUserId(1L);
        order1.setOrderStatus("ACTIVE");
        ordersRepository1.save(order1);



        // Test the method
        Orders foundOrder = ordersRepository1.findFirstByIdAndUserIdAndStatus(1L, 1L, "ACTIVE");

        // Verify the results
        Assertions.assertEquals(1L, foundOrder.getId());
        Assertions.assertEquals(1L, foundOrder.getUserId());
        Assertions.assertEquals("ACTIVE", foundOrder.getOrderStatus());
    }



    @Test
    void testFindAllByUserIdOrderByUpdatedAtDesc() {


        when(ordersRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId())).thenReturn(Arrays.asList(order2, order1));

        List<Orders> orders = ordersRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId());

        assertEquals(2, orders.size());
        assertEquals(order2, orders.get(0));
        assertEquals(order1, orders.get(1));
    }






}