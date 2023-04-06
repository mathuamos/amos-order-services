package com.jagaad.jagaadorderservices.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderDto {
    private Long userId;
    private Long orderId;
    private String reason;
}
