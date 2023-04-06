package com.jagaad.jagaadorderservices.repositories;

import com.jagaad.jagaadorderservices.entities.Cart;
import com.jagaad.jagaadorderservices.entities.CartItems;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
class CartItemsRepositoryTest {

    @Autowired
    CartItemsRepository cartItemsRepository;

    @Autowired
    CartRepository cartRepository;
    private Cart cart =new Cart();

    @BeforeEach
    void setUp() {

        cart.setRecipesCount(1);
        cart.setStatus(RecordStatus.ACTIVE.toString());
        cart.setUserId(1L);
        cart.setCreatedAt(new Date());
        cartRepository.save(cart);
    }

    @AfterEach
    void tearDown() {
        cartItemsRepository.deleteAll();
    }

    @Test
     void testFindFirstByCartIdAndRecipeIdAndStatus() {



        CartItems cartItem =new CartItems();
        cartItem.setCartId(cart.getId());
        cartItem.setId(1L);
        cartItem.setRecipeId(1L);
        cartItem.setPricePerPilote(BigDecimal.valueOf(1.33));
        cartItem.setTotalAmount(BigDecimal.valueOf(10));
        cartItem.setPilotesCount(5);
        cartItem.setStatus(RecordStatus.ACTIVE.toString());
        cartItem.setCreatedAt(new Date());
        cartItem.setUpdatedAt(new Date());
        cartItemsRepository.save(cartItem);

        CartItems response = cartItemsRepository.findFirstByIdAndCartIdAndStatus(cartItem.getId(), cartItem.getCartId(), RecordStatus.ACTIVE.toString());

        assertEquals(response.getId(),cartItem.getId());
    }


    @Test
    void testfindAllByCartIdAndStatus() {



        CartItems cartItem =new CartItems();
        cartItem.setCartId(cart.getId());
        cartItem.setId(1L);
        cartItem.setRecipeId(1L);
        cartItem.setPricePerPilote(BigDecimal.valueOf(1.33));
        cartItem.setTotalAmount(BigDecimal.valueOf(10));
        cartItem.setPilotesCount(5);
        cartItem.setStatus(RecordStatus.ACTIVE.toString());
        cartItem.setCreatedAt(new Date());
        cartItem.setUpdatedAt(new Date());
        cartItemsRepository.save(cartItem);

        List<CartItems>  response = cartItemsRepository.findAllByCartIdAndStatus(cart.getId(), RecordStatus.ACTIVE.toString());

        assertEquals(response.size() , 1);
    }



}
