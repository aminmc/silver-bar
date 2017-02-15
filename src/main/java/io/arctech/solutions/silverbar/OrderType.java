package io.arctech.solutions.silverbar;

import java.math.BigDecimal;

public enum OrderType {

    BUY {
        @Override
        int sort(BigDecimal priceA, BigDecimal priceB) {
            return priceB.compareTo(priceA); //Highest price first
        }
    },
    SELL {
        @Override
        int sort(BigDecimal priceA, BigDecimal priceB) {
            return priceA.compareTo(priceB); //Lowest price first
        }
    };

    abstract int sort(BigDecimal priceA, BigDecimal priceB);

}