package com.jagaad.jagaadorderservices.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RemoveProductFromCartDto {
    private Long  userId;
    private Long  cartId;
    private Long  recipeId;
}
