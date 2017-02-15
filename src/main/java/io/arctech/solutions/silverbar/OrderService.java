package io.arctech.solutions.silverbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.*;

public class OrderService {

    private final ConcurrentMap<Long, Order> simpleOrderDataStore = new ConcurrentHashMap<>();
    private final AtomicLong simpleIdGenerator = new AtomicLong(0);

    private final static double INITIAL = 0;

    public Long create(String userId, double quantity, BigDecimal price, OrderType orderType) {
        validate(userId, quantity, price, orderType);
        Order order = new Order(simpleIdGenerator.incrementAndGet(), userId, quantity, price, orderType);
        simpleOrderDataStore.put(order.getId(), order);
        return order.getId();
    }


    public void cancel(Long orderId) {
        simpleOrderDataStore.remove(orderId);
    }

    public List<Order> listAllOrders() {
        return Collections.unmodifiableList(new ArrayList<>(simpleOrderDataStore.values()));
    }

    public List<OrderSummary> liveOrderSummaries() {
        return Arrays.asList(OrderType.values())
                .stream()
                .map(this::aggregateOrderQuantitiesByType)
                .flatMap(List::stream)
                .collect(toList());
    }

    private List<OrderSummary> aggregateOrderQuantitiesByType(OrderType orderType) {
        return simpleOrderDataStore.values().stream()
                .filter(o -> o.getOrderType() == orderType)
                .collect(groupingBy(Order::getPrice
                        , reducing(INITIAL
                                , Order::getQuantity
                                , Double::sum)))
                .entrySet()
                .stream()
                .map(entry -> new OrderSummary(entry.getValue(), entry.getKey(), orderType))
                .sorted()
                .collect(toList());
    }

    private void validate(String userId, double quantity, BigDecimal price, OrderType orderType) {
        if (userId == null || userId.equals("")) throw new IllegalArgumentException("User id cannot be null or empty");
        if (quantity == 0) throw new IllegalArgumentException("Quantity has to be greater than zero");
        if (price == null || price.equals(BigDecimal.ZERO))
            throw new IllegalArgumentException("Price has to be greater than zero");
        if (orderType == null) throw new IllegalArgumentException("Order type required");
    }

}
