package com.shoppingcart.entities;


import com.shoppingcart.utils.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Orders extends AbstractBaseEntity implements Serializable {
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "items_count")
    private Integer itemsCount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "order_status")
    private String orderStatus= OrderStatus.PENDING.toString();

    @Column(name = "comment")
    private String comment;

}
