package com.jagaad.jagaadorderservices.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cart")
@Getter
@Setter
public class Cart extends AbstractBaseEntity implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    
    @Column(name = "recipes_count")
    private Integer recipesCount;

    @JoinColumn(name = "user_id",referencedColumnName = "id",insertable = false,updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Users userLink;


}
