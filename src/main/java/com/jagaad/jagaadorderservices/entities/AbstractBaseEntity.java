package com.jagaad.jagaadorderservices.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jagaad.jagaadorderservices.utils.RecordStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class   AbstractBaseEntity {


    //has some common columns shared by other entities

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status = RecordStatus.ACTIVE.toString();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @JsonIgnore
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

}
