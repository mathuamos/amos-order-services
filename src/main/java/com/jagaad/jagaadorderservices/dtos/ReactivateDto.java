package com.jagaad.jagaadorderservices.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReactivateDto {

    @NotNull(message = "orderId is mandatory")
    private Long orderId;
}
