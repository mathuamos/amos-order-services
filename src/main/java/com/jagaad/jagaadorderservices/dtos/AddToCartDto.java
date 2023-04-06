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
public class AddToCartDto {


    @NotNull(message = "Recipe Id is mandatory")
    private Long recipeId;
    @NotNull(message = "Number of pilots is mandatory")
    private Integer numberOfPilotes;

}
