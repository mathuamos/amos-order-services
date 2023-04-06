package com.jagaad.jagaadorderservices.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.jagaad.jagaadorderservices.dtos.ModifyCartItemDto;
import com.jagaad.jagaadorderservices.dtos.RemoveCartItemFromCartDto;
import com.jagaad.jagaadorderservices.services.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class CartControllerTest {

    @Mock
    private CartService cartService;

    private CartController cartController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        cartController = new CartController(cartService);
    }




    @Test
    public void testRemoveRecipeFromCart() {
        RemoveCartItemFromCartDto removeCartItemFromCartDto = new RemoveCartItemFromCartDto();
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(cartService.removeProductFromCart(removeCartItemFromCartDto)).thenReturn(responseEntity);

        ResponseEntity<?> response = cartController.removeRecipeFromCart(removeCartItemFromCartDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testModifyCartItem() {
        ModifyCartItemDto modifyCartItemDto = new ModifyCartItemDto();
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(cartService.modifyCartItemDetails(modifyCartItemDto)).thenReturn(responseEntity);

        ResponseEntity<?> response = cartController.removeRecipeFromCart(modifyCartItemDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}