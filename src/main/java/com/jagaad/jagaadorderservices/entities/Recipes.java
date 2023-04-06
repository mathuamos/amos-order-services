package com.shoppingcart.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Products extends AbstractBaseEntity implements Serializable {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "priority")
    private Integer priority;
}
