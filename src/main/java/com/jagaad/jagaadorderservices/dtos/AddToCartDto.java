package com.shoppingcart.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto {
    private Long productId;
    private Long userId;
    private Integer quantity;
}
