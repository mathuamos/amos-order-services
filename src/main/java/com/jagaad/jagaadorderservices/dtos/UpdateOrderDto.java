package com.jagaad.jagaadorderservices.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateOrderDto {

    @NotNull(message = "Recipe Id is mandatory")
    private String paymentMethod;


    @NotNull(message = "address Id is mandatory")
    private String address;


    private String comment;

    @NotNull(message = "orderId Id is mandatory")
    private Long orderId;
}
