package com.jagaad.jagaadorderservices.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
public class Users extends AbstractBaseEntity implements Serializable{

    @Column(name = "first_name")
     private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;


    @Column(name = "email")
    private String email;







}
