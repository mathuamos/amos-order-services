package com.jagaad.jagaadorderservices.repositories;

import com.jagaad.jagaadorderservices.configs.SecurityConfigTest;
import com.jagaad.jagaadorderservices.entities.Cart;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class CartRepositoryTest {



    @Autowired
    CartRepository cartRepository;

    @AfterEach
    void tearDown() {
    }

    @Test
    void findFirstByUserIdAndStatus() {

        Cart cart=new Cart();
        cart.setRecipesCount(1);
        cart.setStatus(RecordStatus.ACTIVE.toString());
        cart.setUserId(1L);
        cart.setCreatedAt(new Date());
        cartRepository.save(cart);


        Cart response = cartRepository.findFirstByUserIdAndStatus(cart.getUserId(), RecordStatus.ACTIVE.toString());

        assertEquals(response.getId(),cart.getId());

    }
}