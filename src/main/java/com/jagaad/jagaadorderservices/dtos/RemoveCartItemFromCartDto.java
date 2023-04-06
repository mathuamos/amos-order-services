package com.jagaad.jagaadorderservices.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveCartItemFromCartDto {

    @NotNull(message = "cartId Id is mandatory")
    private Long  cartId;

    @NotNull(message = "cartItemId Id is mandatory")
    private Long  cartItemId;
}
