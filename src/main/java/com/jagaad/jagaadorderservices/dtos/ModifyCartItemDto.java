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
public class ModifyCartItemDto {


    @NotNull(message = "Number of pilots is mandatory")
    private Integer numberOfPilotes;

    @NotNull(message = "cartItemId Id is mandatory")
    private Long  cartItemId;
}
