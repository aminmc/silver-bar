package io.arctech.solutions.silverbar;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Order {

    private Long id;
    private String userId;
    private Double quantity;
    private BigDecimal price;
    private OrderType orderType;

}
