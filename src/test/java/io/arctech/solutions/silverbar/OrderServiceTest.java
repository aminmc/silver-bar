package io.arctech.solutions.silverbar;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.arctech.solutions.silverbar.OrderType.BUY;
import static io.arctech.solutions.silverbar.OrderType.SELL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OrderServiceTest {

    private OrderService underTest;


    @Before
    public void init() {
        underTest = new OrderService();
    }


    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownForMissingUserId() {
        underTest.create(null, 10.0, new BigDecimal(301), BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownForZeroQuantity() {
        underTest.create("userIdA", 0, new BigDecimal(301), BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownForZeroPrice() {
        underTest.create("userIdA", 10, BigDecimal.ZERO, BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownForMissingOrderType() {
        underTest.create("userIdA", 10, BigDecimal.ZERO, null);
    }


    @Test
    public void canCreateOrder() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(301), BUY);

        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(1));
    }

    @Test
    public void canCancelOrder() {
        //given
        Long orderId = underTest.create("userIdA", 10.0, new BigDecimal(301), BUY);
        underTest.cancel(orderId);

        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(0));
    }

    @Test
    public void canReturnEmptySummariesForNoData() {
        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(0));

    }


    @Test
    public void canGetAggregatedPriceSummaries() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(301), BUY);
        underTest.create("userIdB", 12.0, new BigDecimal(301), BUY);
        underTest.create("userIdC", 100.0, new BigDecimal(120), BUY);

        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(2));
        assertThat(orderSummaries.get(0).getQuantity(), is (22.0));
        assertThat(orderSummaries.get(1).getQuantity(), is (100.0));

    }

    @Test
    public void canGetLowestPriceFirstOnSellSummaryOrders() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(10), SELL);
        underTest.create("userIdB", 12.0, new BigDecimal(12), SELL);
        underTest.create("userIdC", 100.0, new BigDecimal(120), SELL);

        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(3));
        assertThat(orderSummaries.get(0).getPrice(), is(new BigDecimal(10.0)));
        assertThat(orderSummaries.get(1).getPrice(), is(new BigDecimal(12)));
        assertThat(orderSummaries.get(2).getPrice(), is(new BigDecimal(120)));

    }


    @Test
    public void canGetHighestPriceFirstOnBuySummaryOrders() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(10), BUY);
        underTest.create("userIdB", 12.0, new BigDecimal(12), BUY);
        underTest.create("userIdC", 100.0, new BigDecimal(120), BUY);

        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(3));
        assertThat(orderSummaries.get(0).getPrice(), is(new BigDecimal(120)));
        assertThat(orderSummaries.get(1).getPrice(), is(new BigDecimal(12)));
        assertThat(orderSummaries.get(2).getPrice(), is(new BigDecimal(10)));

    }

    @Test
    public void canGetBuyAndSellSummaryOrders() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(10), BUY);
        underTest.create("userIdB", 12.0, new BigDecimal(12), BUY);
        underTest.create("userIdC", 100.0, new BigDecimal(120), SELL);
        underTest.create("userIdC", 100.0, new BigDecimal(120), SELL);


        //when
        List<OrderSummary> orderSummaries = underTest.liveOrderSummaries();

        //then
        assertThat(orderSummaries.size(), is(3));
        assertThatOrderSummariesContainOrderType(orderSummaries, BUY);
        assertThatOrderSummariesContainOrderType(orderSummaries, SELL);
    }

    @Test
    public void canListAllOrders() {
        //given
        underTest.create("userIdA", 10.0, new BigDecimal(10), BUY);
        underTest.create("userIdB", 12.0, new BigDecimal(12), BUY);
        underTest.create("userIdC", 100.0, new BigDecimal(120), SELL);
        underTest.create("userIdC", 100.0, new BigDecimal(120), SELL);

        //when
        List<Order> allOrders = underTest.listAllOrders();

        //then
        assertThat(allOrders.size(), is(4));
    }

    private void assertThatOrderSummariesContainOrderType(List<OrderSummary> orderSummaries,
                                                          OrderType orderType) {
        boolean contains = orderSummaries.stream().anyMatch(o -> o.getOrderType() == orderType);
        assertThat(contains, is(true));
    }
}
