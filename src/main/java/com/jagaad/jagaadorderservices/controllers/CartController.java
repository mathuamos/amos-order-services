package com.jagaad.jagaadorderservices.controllers;


import com.jagaad.jagaadorderservices.dtos.AddToCartDto;
import com.jagaad.jagaadorderservices.dtos.ModifyCartItemDto;
import com.jagaad.jagaadorderservices.dtos.RemoveCartItemFromCartDto;
import com.jagaad.jagaadorderservices.services.CartService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController

public class CartController {
    private final CartService cartService;

    //this controller managers all cart operations

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }



    @GetMapping("/api/v1/recipes")
    public ResponseEntity<?> getRecipres(){
        return cartService.recipes();
    }

    @PostMapping("/api/v1/add-to-cart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartDto addToCartDto){
        return ResponseEntity.ok(cartService.addToCart(addToCartDto));
    }


    @GetMapping("/api/v1/get-cart-details")
    public ResponseEntity<?> addToCart(){
        return ResponseEntity.ok(cartService.cartDetails());
    }


    @PostMapping("/api/v1/remove-recipe")
    public ResponseEntity<?> removeRecipeFromCart(@RequestBody @Valid RemoveCartItemFromCartDto removeCartItemFromCartDto){
        return ResponseEntity.ok(cartService.removeProductFromCart(removeCartItemFromCartDto));
    }

    @PostMapping("/api/v1/modify-cart-item")
    public ResponseEntity<?> removeRecipeFromCart(@RequestBody @Valid ModifyCartItemDto ModifyCartItemDto){
        return ResponseEntity.ok(cartService.modifyCartItemDetails(ModifyCartItemDto));
    }
}
