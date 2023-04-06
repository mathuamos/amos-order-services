package com.jagaad.jagaadorderservices.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDetailsResponseDto {

    private Long cartId;
    private BigDecimal totalAmount;
    private List<ItemDetails> itemDetails;

    @Setter
    @Getter
    public static class ItemDetails {
        private Long id;
        private Long recipeId;
        private String recipeName;
        private BigDecimal pricePerPilotes;
        private Integer noOfPilotes;
    }

}
