package com.jagaad.jagaadorderservices.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CheckoutDto {

    @NotNull(message = "PaymentMethod is mandatory")
    private String paymentMethod;

    @NotNull(message = "address is mandatory")
    private String address;

    private String comment;
}
