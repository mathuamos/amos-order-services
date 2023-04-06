package com.shoppingcart.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutDto {
    private Long userId;
    private String paymentMethod;
}
