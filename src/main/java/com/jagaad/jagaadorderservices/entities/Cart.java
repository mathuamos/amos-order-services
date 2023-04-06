package com.shoppingcart.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "cart")
@Getter
@Setter
public class Cart extends AbstractBaseEntity implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "items_count")
    private Integer itemsCount;


}
