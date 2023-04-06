package com.shoppingcart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDetailsResponseDto {

    private BigDecimal total;
    private BigDecimal totalDiscount;
    private List<ItemDetails> itemDetails;

    @Setter
    @Getter
    public static class ItemDetails {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer discountPercent;
        private BigDecimal discountAmount;
        private Integer quantity;
    }

}
