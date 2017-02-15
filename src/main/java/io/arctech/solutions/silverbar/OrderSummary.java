package io.arctech.solutions.silverbar;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderSummary implements Comparable<OrderSummary> {

    private Double quantity;
    private BigDecimal price;
    private OrderType orderType;

    @Override
    public int compareTo(OrderSummary o) {
        return this.orderType.sort(this.getPrice(), o.getPrice());
    }
}
