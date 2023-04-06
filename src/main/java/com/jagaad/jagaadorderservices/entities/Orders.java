package com.jagaad.jagaadorderservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jagaad.jagaadorderservices.utils.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "address")
    private String address;


    @JsonIgnore
    @JoinColumn(name = "cart_id",referencedColumnName = "id",insertable = false,updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Cart cartLink;

    @JoinColumn(name = "user_id",referencedColumnName = "id",insertable = false,updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Users userLink;

}
