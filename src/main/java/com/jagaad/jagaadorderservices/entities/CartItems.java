package com.shoppingcart.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItems extends AbstractBaseEntity implements Serializable {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @JoinColumn(name = "product_id",referencedColumnName = "id",insertable = false,updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Products productsLink;
}
