package com.jagaad.jagaadorderservices.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
public class ApplicationProperties {

    @Value("${supported.pilotes.count}")
    private List<Long> supportedPilotsCount;


    @Value("${update.order.within.minutes}")
    private Integer updateOrderWithinMinutes;
}
