package com.jagaad.jagaadorderservices.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
public class CartItems extends AbstractBaseEntity implements Serializable {


    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "price_per_pilote")
    private BigDecimal pricePerPilote;

    @Column(name = "pilotes_count")
    private Integer pilotesCount;

    @JoinColumn(name = "recipe_id",referencedColumnName = "id",insertable = false,updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Recipes recipeLink;
}


